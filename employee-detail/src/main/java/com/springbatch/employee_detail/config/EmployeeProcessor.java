package com.springbatch.employee_detail.config;

import com.springbatch.employee_detail.entity.EmployeeDetails;
import org.springframework.batch.item.ItemProcessor;

public class EmployeeProcessor implements ItemProcessor<EmployeeDetails,EmployeeDetails> {


    @Override
    public EmployeeDetails process(EmployeeDetails employeeDetails) throws Exception {

        employeeDetails.setFirstName(employeeDetails.getFirstName().toUpperCase());
        employeeDetails.setLastName(employeeDetails.getLastName().toUpperCase());

        return employeeDetails;
    }
}
