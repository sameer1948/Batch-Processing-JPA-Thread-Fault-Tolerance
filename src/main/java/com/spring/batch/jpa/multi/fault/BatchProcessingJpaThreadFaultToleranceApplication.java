package com.spring.batch.jpa.multi.fault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchProcessingJpaThreadFaultToleranceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchProcessingJpaThreadFaultToleranceApplication.class, args);
	}

}
