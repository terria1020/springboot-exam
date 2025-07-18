package com.batchexam.batchexam.controller;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.batchexam.batchexam.service.BatchExecuteService;

@RestController
@RequestMapping("/api/v1/batch")
public class JobExecController {

  @Autowired
  private BatchExecuteService batchService; // 배치 작업 실행 로직을 처리하는 서비스

  /**
   * 제공된 batchId에 따라 배치 작업을 비동기적으로 실행합니다.
   * 
   * @param batchId 실행할 배치 작업의 식별자
   */
  @GetMapping("")
  public void runBatchExampleAsync(
      @RequestParam(required = true) Integer batchId) throws JobExecutionAlreadyRunningException, JobRestartException,
      JobInstanceAlreadyCompleteException, JobParametersInvalidException {
    batchService.runBatchExampleAsync(batchId);
  }

  /**
   * 복잡한 배치 작업을 비동기적으로 실행합니다.
   */
  @GetMapping("/complex")
  public void runBatchComplex() throws JobExecutionAlreadyRunningException, JobRestartException,
      JobInstanceAlreadyCompleteException, JobParametersInvalidException {
    System.out.println("[ASYNC] 복잡한 배치 작업을 비동기적으로 실행합니다.");
    batchService.runBatchComplexAsync();
  }

  /**
   * 청크 기반 배치 작업을 비동기적으로 실행합니다.
   */
  @GetMapping("/chunk")
  public void runCsvChunk() throws JobExecutionAlreadyRunningException, JobRestartException,
      JobInstanceAlreadyCompleteException, JobParametersInvalidException {
    batchService.runCsvChunkAsync();
  }
}
