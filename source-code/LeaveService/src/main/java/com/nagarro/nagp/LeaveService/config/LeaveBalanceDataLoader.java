package com.nagarro.nagp.LeaveService.config;

import com.nagarro.nagp.LeaveService.entity.LeaveBalance;
import com.nagarro.nagp.LeaveService.entity.LeaveBalanceId;
import com.nagarro.nagp.LeaveService.enums.LeaveType;
import com.nagarro.nagp.LeaveService.repository.LeaveBalanceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeaveBalanceDataLoader {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @PostConstruct
    public void loadLeaveBalances() {
        loadEmployeeLeaves("EMP001");
        loadEmployeeLeaves("EMP002");
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
//        leaveBalance.setMaxCarryForwardAllowed(5);
        leaveBalanceRepository.save(leaveBalance);
    }
}
