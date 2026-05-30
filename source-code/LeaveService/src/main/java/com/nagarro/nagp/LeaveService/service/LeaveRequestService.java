package com.nagarro.nagp.LeaveService.service;

import com.nagarro.nagp.LeaveService.dto.ApplyLeaveRequestDto;
import com.nagarro.nagp.LeaveService.dto.LeaveApprovalRequestDto;
import com.nagarro.nagp.LeaveService.dto.LeaveRequestResponseDto;
import com.nagarro.nagp.LeaveService.enums.LeaveStatus;
import com.nagarro.nagp.LeaveService.enums.LeaveType;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestService {

    Page<LeaveRequestResponseDto> getLeaveRequestHistory(
            String employeeCode,
            LeaveStatus status,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size,
            String sortBy,
            String direction);

    List<LeaveRequestResponseDto> getTeamLeaveRequests(
            String managerCode,
            LeaveStatus status,
            String employeeCode,
            LocalDate startDate,
            LocalDate endDate);

    void applyLeave(
            String employeeCode,
            ApplyLeaveRequestDto leaveRequest);

    void updateLeaveStatus(
            String leaveRequestId,
            String managerCode,
            LeaveApprovalRequestDto leaveApprovalRequestDto);
}
