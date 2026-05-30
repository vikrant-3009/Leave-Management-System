package com.nagarro.nagp.LeaveService.service;

import com.nagarro.nagp.LeaveService.dto.LeaveBalanceResponseDto;
import com.nagarro.nagp.LeaveService.enums.LeaveType;

import java.util.List;

public interface LeaveBalanceService {

    List<LeaveBalanceResponseDto> getEmployeeLeaveBalances(String empCode);

    List<LeaveBalanceResponseDto> getTeamLeaveBalances(String managerCode);

    LeaveBalanceResponseDto getEmployeeLeaveBalanceByType(String empCode, LeaveType leaveType);
}
