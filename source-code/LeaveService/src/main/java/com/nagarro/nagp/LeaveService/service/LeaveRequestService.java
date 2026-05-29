package com.nagarro.nagp.LeaveService.service;

import com.nagarro.nagp.LeaveService.dto.ApplyLeaveRequestDto;

public interface LeaveRequestService {

    void applyLeave(String employeeCode, ApplyLeaveRequestDto leaveRequest);
}
