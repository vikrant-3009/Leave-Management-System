package com.nagarro.nagp.LeaveService.service;

import com.nagarro.nagp.LeaveService.dto.LeaveBalanceResponseDto;
import com.nagarro.nagp.LeaveService.enums.LeaveType;

import java.util.List;

public interface LeaveBalanceService {

    public List<LeaveBalanceResponseDto> getEmployeeLeaveBalances(String empCode);

    public List<LeaveBalanceResponseDto> getTeamLeaveBalances(String managerCode);

    public LeaveBalanceResponseDto getEmployeeLeaveBalanceByType(String empCode, LeaveType leaveType);
}
