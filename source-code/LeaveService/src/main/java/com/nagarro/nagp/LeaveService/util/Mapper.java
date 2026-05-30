package com.nagarro.nagp.LeaveService.util;

import com.nagarro.nagp.LeaveService.dto.LeaveBalanceResponseDto;
import com.nagarro.nagp.LeaveService.dto.LeaveRequestResponseDto;
import com.nagarro.nagp.LeaveService.entity.LeaveBalance;
import com.nagarro.nagp.LeaveService.entity.LeaveRequest;

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

    public static LeaveRequestResponseDto mapLeaveRequestToLeaveRequestResponseDto(
            LeaveRequest leaveRequest) {
        return LeaveRequestResponseDto.builder()
                .leaveRequestId(leaveRequest.getId())
                .employeeCode(leaveRequest.getEmployee().getEmployeeCode())
                .employeeName(leaveRequest.getEmployee().getEmployeeName())
                .leaveType(leaveRequest.getLeaveType())
                .startDate(leaveRequest.getStartDate())
                .endDate(leaveRequest.getEndDate())
                .numberOfDays(leaveRequest.getNumberOfDays())
                .reason(leaveRequest.getReason())
                .status(leaveRequest.getStatus())
                .reportingManagerCode(leaveRequest.getReportingManager().getEmployeeCode())
                .reportingManagerName(leaveRequest.getReportingManager().getEmployeeName())
                .lastModifiedAt(leaveRequest.getLastModifiedAt())
                .lastModifiedBy(leaveRequest.getLastModifiedBy())
                .build();
    }
}
