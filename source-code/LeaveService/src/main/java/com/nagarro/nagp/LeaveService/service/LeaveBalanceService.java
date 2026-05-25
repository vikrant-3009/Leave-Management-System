package com.nagarro.nagp.LeaveService.service;

import com.nagarro.nagp.LeaveService.dto.LeaveBalanceResponseDto;

import java.util.List;

public interface LeaveBalanceService {

    public List<LeaveBalanceResponseDto> getEmployeeLeaveBalances(String empCode);
}
