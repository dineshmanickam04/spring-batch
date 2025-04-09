package com.springbatch.employee_detail.repository;

import com.springbatch.employee_detail.entity.EmployeeDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeDetails,Long> {
}
