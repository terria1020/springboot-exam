package com.batchexam.batchexam.taskletBatch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import com.batchexam.batchexam.dto.DataModelDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ComplexBatch {

  @Autowired
  private JobRepository jobRepository; // 배치 작업 실행을 관리하는 저장소

  @Autowired
  private PlatformTransactionManager platformTransactionManager; // 배치 스텝을 위한 트랜잭션 관리자

  /**
   * CSV 파일에서 데이터를 읽고 처리하는 태스크릿 기반 스텝을 정의합니다.
   */
  @Bean
  public Step complexStep() {

    return new StepBuilder("complexStep", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          Resource csvResource = new ClassPathResource("data/data.csv");
          try (BufferedReader reader = new BufferedReader(
              new InputStreamReader(csvResource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            ObjectMapper mapper = new ObjectMapper();
            reader.readLine(); // 헤더 라인 건너뜀
            Integer index = 0;
            while ((line = reader.readLine()) != null) {
              Thread.sleep(100);
              String[] parts = line.split(",");
              DataModelDto dto = new DataModelDto(parts[1], parts[2], parts[3]);
              String json = mapper.writeValueAsString(dto);
              System.out.println("[%s] 데이터: %s".formatted("" + index, json));
            }
          }
          return RepeatStatus.FINISHED;
        }, platformTransactionManager)
        .build();
  }

  /**
   * 복잡한 스텝을 실행하는 배치 작업을 정의합니다.
   */
  @Bean("complexJob")
  public Job complexJob() {
    return new JobBuilder("complexJob", jobRepository)
        .incrementer(new RunIdIncrementer())
        .start(complexStep())
        .build();
  }

}
