package com.nagarro.nagp.LeaveService.service.impl;

import com.nagarro.nagp.LeaveService.dto.ApplyLeaveRequestDto;
import com.nagarro.nagp.LeaveService.entity.Employee;
import com.nagarro.nagp.LeaveService.entity.LeaveBalance;
import com.nagarro.nagp.LeaveService.entity.LeaveBalanceId;
import com.nagarro.nagp.LeaveService.entity.LeaveRequest;
import com.nagarro.nagp.LeaveService.enums.LeaveStatus;
import com.nagarro.nagp.LeaveService.enums.LeaveType;
import com.nagarro.nagp.LeaveService.exception.DateNotValidException;
import com.nagarro.nagp.LeaveService.exception.UserNotFoundException;
import com.nagarro.nagp.LeaveService.repository.EmployeeRepository;
import com.nagarro.nagp.LeaveService.repository.LeaveBalanceRepository;
import com.nagarro.nagp.LeaveService.repository.LeaveRequestRepository;
import com.nagarro.nagp.LeaveService.service.LeaveRequestService;
import com.nagarro.nagp.LeaveService.util.LeaveDateUtil;
import com.nagarro.nagp.LeaveService.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public void applyLeave(String employeeCode, ApplyLeaveRequestDto leaveRequestDto)
            throws UserNotFoundException, DateNotValidException {

        // validate employee (exists)
        // validate manager (exists as well as should be the manager of the employee)
        // validate dates (Past Date and startDate <= endDate check)
        // validate for overlapping leave requests
        // validate for sufficient leave balances

        Employee employee =
                employeeRepository
                        .findById(employeeCode)
                        .orElseThrow(() -> new UserNotFoundException("Invalid employee code"));

        Employee manager =
                employeeRepository
                        .findById(leaveRequestDto.getReportingManagerCode())
                        .orElseThrow(() -> new UserNotFoundException("Invalid manager code"));

        if (!employee.getManager().getEmployeeCode().equals(manager.getEmployeeCode())) {
            throw new UserNotFoundException("Invalid manager for the employee");
        }

        ValidationUtil.validateDates(leaveRequestDto.getStartDate(), leaveRequestDto.getEndDate());

        validateOverlappingLeaveRequests(
                employeeCode,
                leaveRequestDto.getStartDate(),
                leaveRequestDto.getEndDate()
        );

        int numberOfLeaveDays =
                LeaveDateUtil.calculateWorkingDays(
                        leaveRequestDto.getStartDate(),
                        leaveRequestDto.getEndDate());

        validateLeaveBalance(employeeCode, leaveRequestDto.getLeaveType(), numberOfLeaveDays);

        LeaveRequest leaveRequest =
                LeaveRequest.builder()
                        .employee(employee)
                        .leaveType(leaveRequestDto.getLeaveType())
                        .startDate(leaveRequestDto.getStartDate())
                        .endDate(leaveRequestDto.getEndDate())
                        .reason(leaveRequestDto.getReason())
                        .reportingManager(manager)
                        .status(LeaveStatus.PENDING)
                        .build();

        leaveRequestRepository.save(leaveRequest);
    }

    private void validateOverlappingLeaveRequests(
            String empCode,
            LocalDate startDate,
            LocalDate endDate) throws DateNotValidException {

        boolean exists = leaveRequestRepository.hasOverlappingLeaveRequests(empCode, startDate, endDate);

        if (exists) {
            throw new DateNotValidException("Overlapping leave request exists");
        }
    }

    private void validateLeaveBalance(
            String empCode,
            LeaveType leaveType,
            Integer daysRequested) throws DateNotValidException {

        LeaveBalanceId id = new LeaveBalanceId(empCode, leaveType);

        LeaveBalance leaveBalance =
                leaveBalanceRepository
                        .findById(id)
                        .orElseThrow(() -> new DateNotValidException("Leave Balance not found"));

        if (leaveBalance.getRemainingLeaves() <= daysRequested) {
            throw new DateNotValidException("Insufficient leave balance");
        }
    }
}
