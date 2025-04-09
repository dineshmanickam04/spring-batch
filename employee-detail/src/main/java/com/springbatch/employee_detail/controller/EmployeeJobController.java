package com.springbatch.employee_detail.controller;


import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RestController
@RequestMapping("/jobs")
public class EmployeeJobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job1;

    @PostMapping("/import-data")
    public String jobLauncher() {

        final JobParameters jobParameters = new JobParametersBuilder()
                .addDate("date", Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .addString("employee-job",job1.getName())
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();
        try {
            // Launch the job
            final JobExecution jobExecution = jobLauncher.run(job1, jobParameters);

            // Return job status
            return jobExecution.getStatus().toString();
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException e) {
            e.printStackTrace();
            return "Job failed with exception: " + e.getMessage();
        }
    }
}

