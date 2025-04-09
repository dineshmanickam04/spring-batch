package com.springbatch.employee_detail.config;


import com.springbatch.employee_detail.entity.EmployeeDetails;
import com.springbatch.employee_detail.repository.EmployeeRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SpringBatchConfig {

    @Autowired
    private EmployeeRepository employeeRepository;


    @Bean
    public FlatFileItemReader<EmployeeDetails> reader() {
        return new FlatFileItemReaderBuilder<EmployeeDetails>()
                .name("employeeItemReader")
                .resource(new ClassPathResource("employee-details.csv"))
                .linesToSkip(1)
                .lineMapper(lineMapper())
                .targetType(EmployeeDetails.class)
                .build();
    }
    private LineMapper<EmployeeDetails> lineMapper() {
        DefaultLineMapper<EmployeeDetails> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames( "emp_id","firstName", "lastName", "emailId", "gender", "mobile_number");

        BeanWrapperFieldSetMapper<EmployeeDetails> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(EmployeeDetails.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    EmployeeProcessor processor() {
        return new EmployeeProcessor();
    }

    @Bean
    RepositoryItemWriter<EmployeeDetails> writer() {
        RepositoryItemWriter<EmployeeDetails> writer = new RepositoryItemWriter<>();
        writer.setRepository(employeeRepository);
        writer.setMethodName("saveAndFlush");
        return writer;
    }


    @Bean
    public Job job(JobRepository jobRepository, Step step) {
        return new JobBuilder("importEmployees", jobRepository)
                .start(step)
                .build();
    }


    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);       // Minimum number of threads always alive
        executor.setMaxPoolSize(10);        // Maximum threads when under high load
        executor.setQueueCapacity(100);     // Queue to hold chunks before they're picked up by threads
        executor.setThreadNamePrefix("batch-thread-");
       // Helpful for debugging
        executor.initialize();
        return executor;
    }
    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("csv-import-step", jobRepository)
                .<EmployeeDetails, EmployeeDetails>chunk(25, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }





}
