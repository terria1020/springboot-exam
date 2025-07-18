package com.batchexam.batchexam.chunkBatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import com.batchexam.batchexam.dto.DataModelDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ChunkBatch {
  // 1) Reader: CSV 파일에서 데이터를 읽음
  @Bean
  public FlatFileItemReader<DataModelDto> csvReader() {
    FlatFileItemReader<DataModelDto> reader = new FlatFileItemReader<>();
    // CSV 파일의 경로를 설정합니다.
    reader.setResource(new ClassPathResource("data/data.csv"));
    // 첫 번째 줄(헤더)을 건너뜁니다.
    reader.setLinesToSkip(1);
    // CSV 파일의 각 열 이름을 설정합니다.
    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
    tokenizer.setNames("id", "firstName", "lastName", "email");
    // CSV 데이터를 객체로 매핑하기 위한 LineMapper를 설정합니다.
    DefaultLineMapper<DataModelDto> mapper = new DefaultLineMapper<>();
    mapper.setLineTokenizer(tokenizer);

    // 각 필드를 DataModelDto 객체로 매핑합니다.
    mapper.setFieldSetMapper((fieldSet) -> {
      return new DataModelDto(fieldSet.readString("firstName"), fieldSet.readString("lastName"),
          fieldSet.readString("email"));
    });
    reader.setLineMapper(mapper);
    return reader;
  }

  // 2) Processor: 데이터를 처리 (현재는 그대로 전달)
  @Bean
  public ItemProcessor<DataModelDto, DataModelDto> processor() {
    // 데이터를 그대로 반환하는 프로세서입니다.
    return dto -> dto;
  }

  // 3) Writer: 데이터를 JSON 형식으로 출력
  @Bean
  public ItemWriter<DataModelDto> jsonWriter() {
    return items -> {
      // JSON 변환을 위한 ObjectMapper를 생성합니다.
      ObjectMapper mapper = new ObjectMapper();
      System.out.println("=== 청크 시작 ===");
      Integer index = 0;
      for (DataModelDto dto : items) {
        // 각 항목의 데이터를 출력합니다.
        System.out.print("읽음 %s [%s] | ".formatted(dto.firstName(), "" + index++));
        // 처리 속도를 조절하기 위해 잠시 대기합니다.
        Thread.sleep(100);
      }
      System.out.println("\n");
    };
  }

  // 4) Step: Reader, Processor, Writer를 결합하여 청크 기반 스텝 생성
  @Bean
  public Step csvChunkStep(JobRepository jobRepo,
      PlatformTransactionManager tx,
      FlatFileItemReader<DataModelDto> reader,
      ItemProcessor<DataModelDto, DataModelDto> processor,
      ItemWriter<DataModelDto> writer) {
    return new StepBuilder("csvChunkStep", jobRepo)
        // 청크 크기를 설정합니다. (한 번에 50개씩 처리)
        .<DataModelDto, DataModelDto>chunk(50, tx)
        // Reader, Processor, Writer를 설정합니다.
        .reader(reader)
        .processor(processor)
        .writer(writer)
        // 병렬 처리를 위한 TaskExecutor를 설정합니다.
        .taskExecutor(taskExecutor())
        // 동시에 실행할 최대 청크 수를 설정합니다.
        .throttleLimit(2)
        .build();
  }

  // 5) Job: 청크 기반 스텝 실행
  @Bean("csvChunkJob")
  public Job csvChunkJob(JobRepository jobRepository, Step csvChunkStep) {
    return new JobBuilder("csvChunkJob", jobRepository)
        // 실행 ID를 자동으로 증가시키는 Incrementer를 설정합니다.
        .incrementer(new RunIdIncrementer())
        // 첫 번째 스텝을 설정합니다.
        .start(csvChunkStep)
        .build();
  }

  // TaskExecutor: 병렬 처리를 위한 스레드 풀 구성
  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
    // 코어 스레드 수를 설정합니다.
    exec.setCorePoolSize(5);
    // 최대 스레드 수를 설정합니다.
    exec.setMaxPoolSize(5);
    // 작업 대기열의 크기를 설정합니다.
    exec.setQueueCapacity(10);
    // 설정을 적용합니다.
    exec.afterPropertiesSet();
    return exec;
  }
}
