package com.batchexam.batchexam.taskletBatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class ExamBatch {
  @Autowired
  private JobRepository jobRepository; // 배치 작업 실행을 관리하는 저장소

  @Autowired
  private PlatformTransactionManager transactionManager; // 배치 스텝을 위한 트랜잭션 관리자

  /**
   * 콘솔에 메시지를 출력하는 간단한 태스크릿 스텝을 정의합니다.
   */
  @Bean
  public Step exampleStep() {
    // StepBuilder를 생성하여 스텝의 이름과 JobRepository를 설정합니다.
    StepBuilder builder = new StepBuilder("exampleStep", jobRepository);

    return builder
        // Tasklet을 정의합니다. Tasklet은 배치 작업의 단일 작업 단위입니다.
        .tasklet((contrib, chunk) -> {
          // 콘솔에 공백 줄을 출력합니다.
          System.out.println("\n\n\n");
          // 콘솔에 메시지를 출력합니다.
          System.out.println("안녕하세요, 이것은 태스크릿 스텝입니다!");
          // 작업이 완료되었음을 나타냅니다.
          return RepeatStatus.FINISHED;
        }, transactionManager) // 트랜잭션 관리자를 설정합니다.
        .build(); // 스텝을 빌드합니다.
  }

  /**
   * 콘솔에 다른 메시지를 출력하는 또 다른 간단한 태스크릿 스텝을 정의합니다.
   */
  @Bean
  public Step exampleStep2() {
    // StepBuilder를 생성하여 스텝의 이름과 JobRepository를 설정합니다.
    StepBuilder builder = new StepBuilder("exampleStep2", jobRepository);

    return builder
        // Tasklet을 정의합니다. Tasklet은 배치 작업의 단일 작업 단위입니다.
        .tasklet((contrib, chunk) -> {
          // 콘솔에 공백 줄을 출력합니다.
          System.out.println("\n\n\n");
          // 콘솔에 메시지를 출력하며, contrib 객체에서 읽은 데이터 수를 포함합니다.
          System.out.println("이것은 또 다른 태스크릿 스텝입니다! %s".formatted(contrib.getReadCount()));
          // 작업이 완료되었음을 나타냅니다.
          return RepeatStatus.FINISHED;
        }, transactionManager) // 트랜잭션 관리자를 설정합니다.
        .build(); // 스텝을 빌드합니다.
  }

  /**
   * 첫 번째 예제 스텝을 실행하는 배치 작업을 정의합니다.
   */
  @Bean
  public Job exampleJob() {
    return new JobBuilder("exampleJob", jobRepository) // JobBuilder를 생성하여 작업의 이름과 JobRepository를 설정합니다.
        .incrementer(new RunIdIncrementer()) // 작업 실행 시마다 고유 ID를 생성하는 Incrementer를 설정합니다.
        .start(exampleStep()) // 작업의 첫 번째 스텝을 설정합니다.
        .build(); // 작업을 빌드합니다.
  }

  /**
   * 두 번째 예제 스텝을 실행하는 배치 작업을 정의합니다.
   */
  @Bean
  public Job exampleJob2() {
    return new JobBuilder("exampleJob2", jobRepository) // JobBuilder를 생성하여 작업의 이름과 JobRepository를 설정합니다.
        .incrementer(new RunIdIncrementer()) // 작업 실행 시마다 고유 ID를 생성하는 Incrementer를 설정합니다.
        .start(exampleStep2()) // 작업의 첫 번째 스텝을 설정합니다.
        .build(); // 작업을 빌드합니다.
  }
}