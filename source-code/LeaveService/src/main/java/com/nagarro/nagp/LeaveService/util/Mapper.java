package com.nagarro.nagp.LeaveService.util;

import com.nagarro.nagp.LeaveService.dto.LeaveBalanceResponseDto;
import com.nagarro.nagp.LeaveService.entity.LeaveBalance;

public class Mapper {

    public static LeaveBalanceResponseDto mapLeaveBalToLeaveBalResponseDto(
            LeaveBalance leaveBalance) {
        return LeaveBalanceResponseDto.builder()
                .employeeCode(leaveBalance.getId().getEmployeeCode())
                .leaveType(leaveBalance.getId().getLeaveType())
                .accruedLeaves(leaveBalance.getAccruedLeaves())
                .leavesUsed(leaveBalance.getLeavesUsed())
                .remainingLeaves(leaveBalance.getRemainingLeaves())
                .build();
    }
}
