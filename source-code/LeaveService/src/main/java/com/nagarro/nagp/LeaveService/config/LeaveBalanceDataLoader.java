package com.nagarro.nagp.LeaveService.config;

import com.nagarro.nagp.LeaveService.entity.Employee;
import com.nagarro.nagp.LeaveService.entity.LeaveBalance;
import com.nagarro.nagp.LeaveService.entity.LeaveBalanceId;
import com.nagarro.nagp.LeaveService.enums.LeaveType;
import com.nagarro.nagp.LeaveService.repository.EmployeeRepository;
import com.nagarro.nagp.LeaveService.repository.LeaveBalanceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeaveBalanceDataLoader {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostConstruct
    public void loadLeaveBalances() {
        List<Employee> employees = employeeRepository.findAll();
        employees.forEach(emp -> loadEmployeeLeaves(emp.getEmployeeCode()));
    }

    private void loadEmployeeLeaves(String employeeCode) {
        createLeaveIfNotExists(employeeCode, LeaveType.SICK, 10);
        createLeaveIfNotExists(employeeCode, LeaveType.PRIVILEGE, 15);
        createLeaveIfNotExists(employeeCode, LeaveType.CASUAL, 12);
    }

    private void createLeaveIfNotExists(
            String employeeCode,
            LeaveType leaveType,
            int accruedLeaves
    ) {
        LeaveBalanceId id = new LeaveBalanceId(employeeCode, leaveType);
        if (leaveBalanceRepository.existsById(id)) {
            return;
        }
        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setId(id);
        leaveBalance.setAccruedLeaves(accruedLeaves);
        leaveBalance.setLeavesUsed(0);
        leaveBalanceRepository.save(leaveBalance);
    }
}
