package com.batchexam.batchexam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BatchExamApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchExamApplication.class, args);
	}

}
