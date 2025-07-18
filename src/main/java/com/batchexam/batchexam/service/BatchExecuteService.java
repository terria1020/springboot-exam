package com.batchexam.batchexam.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class BatchExecuteService {

  @Autowired
  private JobLauncher jobLauncher; // Spring Batch의 JobLauncher로 배치 작업 실행

  @Autowired
  @Qualifier("exampleJob")
  private Job type1Job; // 예제 배치 작업 1

  @Autowired
  @Qualifier("exampleJob2")
  private Job type2Job; // 예제 배치 작업 2

  @Autowired
  @Qualifier("complexJob")
  private Job complexJob; // 복잡한 배치 작업

  @Autowired
  @Qualifier("csvChunkJob")
  private Job csvChunkJob; // 청크 기반 배치 작업

  /**
   * 제공된 batchId에 따라 배치 작업을 비동기적으로 실행합니다.
   * 
   * @param batchId 실행할 배치 작업의 식별자
   */
  @Async
  public void runBatchExampleAsync(Integer batchId) throws JobExecutionAlreadyRunningException, JobRestartException,
      JobInstanceAlreadyCompleteException, JobParametersInvalidException {

    JobParameters parameters = new JobParametersBuilder()
        .addLong("now", System.currentTimeMillis())
        .toJobParameters();

    if (batchId.equals(1)) {
      jobLauncher.run(type1Job, parameters);
    } else if (batchId.equals(2)) {
      jobLauncher.run(type2Job, parameters);
    }

  }

  /**
   * 복잡한 배치 작업을 비동기적으로 실행합니다.
   */
  @Async
  public void runBatchComplexAsync() throws JobExecutionAlreadyRunningException, JobRestartException,
      JobInstanceAlreadyCompleteException, JobParametersInvalidException {
    JobParameters parameters = new JobParametersBuilder()
        .addLong("now", System.currentTimeMillis())
        .toJobParameters();

    jobLauncher.run(complexJob, parameters);
  }

  /**
   * 청크 기반 배치 작업을 비동기적으로 실행합니다.
   */
  @Async
  public void runCsvChunkAsync() throws JobExecutionAlreadyRunningException, JobRestartException,
      JobInstanceAlreadyCompleteException, JobParametersInvalidException {
    JobParameters params = new JobParametersBuilder()
        .addLong("now", System.currentTimeMillis())
        .toJobParameters();
    jobLauncher.run(csvChunkJob, params);
  }
}
