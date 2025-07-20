package com.batchexam.batchexam.errorHandling;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.batchexam.batchexam.dto.DataModelDto;

@Configuration
public class ErrorHandlingBatch {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    /**
     * 에러 데이터를 읽는 Reader - CSV의 id 필드는 무시하고 나머지만 매핑
     */
    @Bean("errorDataReader")
    public FlatFileItemReader<DataModelDto> reader() {
        return new FlatFileItemReaderBuilder<DataModelDto>()
                .name("errorDataReader")
                .resource(new FileSystemResource("src/main/resources/data/error-data.csv"))
                .delimited()
                .names("id", "firstName", "lastName", "email") // CSV 컬럼명 정의
                .linesToSkip(1) // 헤더 스킵
                .fieldSetMapper(fieldSet -> {
                    // id 필드는 무시하고 DataModelDto 생성
                    return new DataModelDto(
                            fieldSet.readString("firstName"),
                            fieldSet.readString("lastName"),
                            fieldSet.readString("email"));
                })
                .build();
    }

    /**
     * 처리된 데이터를 콘솔에 출력하는 Writer
     */
    @Bean("errorDataWriter")
    public ItemWriter<DataModelDto> dataWriter() {
        return items -> {
            System.out.println("=== Error Handling Chunk 처리 결과 ===");
            Integer index = 0;
            for (DataModelDto dto : items) {
                System.out.printf("✅ [%d] 처리 완료: %s %s (%s)%n",
                        index++, dto.firstName(), dto.lastName(), dto.email());
            }
            System.out.println("=================================\n");
        };
    }

    /**
     * 데이터 검증 및 변환 Processor
     */
    @Bean("errorHandlingProcessor")
    public ErrorHandlingProcessor processor() {
        return new ErrorHandlingProcessor();
    }

    /**
     * Skip Listener
     */
    @Bean("customSkipListener")
    public CustomSkipListener customSkipListener() {
        return new CustomSkipListener();
    }

    /**
     * Retry Listener
     */
    @Bean("customRetryListener")
    public CustomRetryListener customRetryListener() {
        return new CustomRetryListener();
    }

    /**
     * Error Handling을 적용한 Step
     */
    @Bean("errorHandlingStep")
    public Step errorHandlingStep() {
        return new StepBuilder("errorHandlingStep", jobRepository)
                .<DataModelDto, DataModelDto>chunk(5, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(dataWriter())
                // Fault Tolerant 설정
                .faultTolerant()
                // Skip 정책: 특정 예외 발생 시 건너뛰기
                .skip(IllegalArgumentException.class)
                .skip(NumberFormatException.class)
                .skip(org.springframework.batch.item.file.FlatFileParseException.class)
                .skip(org.springframework.batch.item.file.transform.IncorrectTokenCountException.class)
                .skipLimit(10) // 최대 10개까지 skip 허용
                // Retry 정책: 특정 Exception만 재시도
                .retry(CustomRetryableException.class)
                .retryLimit(3) // 최대 3번 재시도
                // Retry 실패 후 Skip 처리
                .skip(org.springframework.retry.RetryException.class)
                // Listener 등록
                .listener(customSkipListener())
                .listener(customRetryListener())
                .build();
    }

    /**
     * Error Handling Job
     */
    @Bean("errorHandlingJob")
    public Job errorHandlingJob() {
        return new JobBuilder("errorHandlingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(errorHandlingStep())
                .build();
    }
}