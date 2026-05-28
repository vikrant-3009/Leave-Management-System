package com.nagarro.nagp.LeaveService.repository;

import com.nagarro.nagp.LeaveService.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    List<Employee> findByManagerEmployeeCode(String managerCode);

    boolean existsByEmployeeCodeAndManagerEmployeeCode(String employeeCode, String managerCode);
}
