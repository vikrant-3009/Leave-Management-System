package com.nagarro.nagp.LeaveService.service.impl;

import com.nagarro.nagp.LeaveService.dto.LeaveBalanceResponseDto;
import com.nagarro.nagp.LeaveService.entity.LeaveBalance;
import com.nagarro.nagp.LeaveService.exception.UserNotFoundException;
import com.nagarro.nagp.LeaveService.repository.LeaveBalanceRepository;
import com.nagarro.nagp.LeaveService.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public List<LeaveBalanceResponseDto> getEmployeeLeaveBalances(String empCode)
            throws UserNotFoundException {

        List<LeaveBalance> balances = leaveBalanceRepository.findByIdEmployeeCode(empCode);

        if (balances.isEmpty()) {
            throw new UserNotFoundException();
        }

        return balances.stream()
                .map(balance -> LeaveBalanceResponseDto.builder()
                        .leaveType(balance.getId().getLeaveType())
                        .accruedLeaves(balance.getAccruedLeaves())
                        .leavesUsed(balance.getLeavesUsed())
                        .remainingLeaves(balance.getRemainingLeaves())
                        .build())
                .toList();
    }
}
