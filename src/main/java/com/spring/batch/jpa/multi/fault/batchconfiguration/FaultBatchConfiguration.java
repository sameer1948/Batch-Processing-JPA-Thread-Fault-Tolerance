package com.spring.batch.jpa.multi.fault.batchconfiguration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.spring.batch.jpa.multi.fault.entity.Customer;
import com.spring.batch.jpa.multi.fault.exceptionskip.ExceptionSkipPolicy;
import com.spring.batch.jpa.multi.fault.listener.MySkipListener;
import com.spring.batch.jpa.multi.fault.repository.Customer_Repository;


@Configuration
@EnableBatchProcessing
public class FaultBatchConfiguration {
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private Customer_Repository customer_Repository;
	
	
	@Bean
	public FlatFileItemReader<Customer> customerReader(){
		
		FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
		
		flatFileItemReader.setName("Customer-File-Reader");
		flatFileItemReader.setResource(new ClassPathResource("customers.csv"));
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		
		return flatFileItemReader;
		
	}


	public LineMapper<Customer> lineMapper() {
		
		DefaultLineMapper<Customer> defaultLineMapper = new DefaultLineMapper<Customer>();
		
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_COMMA);
		delimitedLineTokenizer.setStrict(false);
		delimitedLineTokenizer.setNames("id","firstName","lastName","email","gender","contactNo","country","dob");
		
		BeanWrapperFieldSetMapper<Customer> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<Customer>();
		beanWrapperFieldSetMapper.setTargetType(Customer.class);
		
		defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

		return defaultLineMapper;
	}
	
	
	@Bean
	public ItemProcessor<Customer, Customer> processor(){
		return customer->customer;
	}
	
	
	@Bean
	public RepositoryItemWriter<Customer> repositoryItemWriter(){
		
		RepositoryItemWriter<Customer> repositoryItemWriter = new RepositoryItemWriter<Customer>();
		
		repositoryItemWriter.setRepository(customer_Repository);
		repositoryItemWriter.setMethodName("save");
		
		return repositoryItemWriter;
	}
	
	
	@Bean
	public Step fileToDBJob() {

		return stepBuilderFactory.get("fileToDBJob")
				.<Customer,Customer>chunk(5)
				.reader(customerReader())
				.processor(processor())
				.writer(repositoryItemWriter())
				.faultTolerant()
				.skipLimit(10)
				//.skip(NumberFormatException.class)
				//.noSkip(type)
				.listener(skipListener())
				.skipPolicy(skipPolicy())
				.build();
	}
	
	
	@Bean
	public Job masterJob() {
		return jobBuilderFactory.get("Master_Job")
				.flow(fileToDBJob())
				.end()
				.build();
	}
	
	@Bean
	public SkipPolicy skipPolicy() {
		return new ExceptionSkipPolicy();
	}

	@Bean
	public SkipListener<Customer,Object> skipListener() {
		return new MySkipListener();
	}
}
