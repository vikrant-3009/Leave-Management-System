package com.nagarro.nagp.LeaveService.config;

import com.nagarro.nagp.LeaveService.entity.Employee;
import com.nagarro.nagp.LeaveService.repository.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeDataLoader {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostConstruct
    public void loadEmployees() {
        if (employeeRepository.count() > 0) {
            return;
        }

        // Managers
        Employee manager1 = createEmployee(
                "MGR001",
                "John Doe",
                null
        );
        Employee manager2 = createEmployee(
                "MGR002",
                "Anjali Singh",
                null
        );

        // Employees under Manager 1
        createEmployee(
                "EMP001",
                "Rahul Jain",
                manager1
        );
        createEmployee(
                "EMP002",
                "Jane Doe",
                manager1
        );

        // Employees under Manager 2
        createEmployee(
                "EMP003",
                "Mike Tyson",
                manager2
        );
        createEmployee(
                "EMP004",
                "Priya Verma",
                manager2
        );
    }

    private Employee createEmployee(
            String employeeCode,
            String employeeName,
            Employee manager) {
        Employee employee = Employee.builder()
                .employeeCode(employeeCode)
                .employeeName(employeeName)
                .manager(manager)
                .build();
        return employeeRepository.save(employee);
    }
}
