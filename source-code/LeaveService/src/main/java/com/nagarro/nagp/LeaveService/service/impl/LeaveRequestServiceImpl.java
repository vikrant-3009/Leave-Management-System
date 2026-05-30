package com.nagarro.nagp.LeaveService.service.impl;

import com.nagarro.nagp.LeaveService.dto.ApplyLeaveRequestDto;
import com.nagarro.nagp.LeaveService.dto.LeaveApprovalRequestDto;
import com.nagarro.nagp.LeaveService.dto.LeaveRequestResponseDto;
import com.nagarro.nagp.LeaveService.entity.Employee;
import com.nagarro.nagp.LeaveService.entity.LeaveBalance;
import com.nagarro.nagp.LeaveService.entity.LeaveBalanceId;
import com.nagarro.nagp.LeaveService.entity.LeaveRequest;
import com.nagarro.nagp.LeaveService.enums.LeaveStatus;
import com.nagarro.nagp.LeaveService.enums.LeaveType;
import com.nagarro.nagp.LeaveService.exception.*;
import com.nagarro.nagp.LeaveService.repository.EmployeeRepository;
import com.nagarro.nagp.LeaveService.repository.LeaveBalanceRepository;
import com.nagarro.nagp.LeaveService.repository.LeaveRequestRepository;
import com.nagarro.nagp.LeaveService.service.LeaveRequestService;
import com.nagarro.nagp.LeaveService.specification.LeaveRequestSpecification;
import com.nagarro.nagp.LeaveService.util.LeaveDateUtil;
import com.nagarro.nagp.LeaveService.util.Mapper;
import com.nagarro.nagp.LeaveService.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveRequestResponseDto> getLeaveRequestHistory(
            String empCode,
            LeaveStatus status,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size,
            String sortBy,
            String direction) throws DateNotValidException {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new DateNotValidException("Start date must be before or equal to end date");
        }

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable paging = PageRequest.of(page, size, sort);

        Specification<LeaveRequest> specification =
                LeaveRequestSpecification
                        .employeeLeaveRequestHistory(empCode, status, leaveType, startDate, endDate);

        return leaveRequestRepository
                .findAll(specification, paging)
                .map(Mapper::mapLeaveRequestToLeaveRequestResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDto> getTeamLeaveRequests(
            String managerCode,
            LeaveStatus status,
            String employeeCode,
            LocalDate startDate,
            LocalDate endDate) throws UserNotFoundException, DateNotValidException {

        if (employeeCode != null) {
            Employee employee =
                    employeeRepository
                            .findById(employeeCode)
                            .orElseThrow(() ->
                                    new UserNotFoundException("Invalid employee code"));

            if (!employee.getManager().getEmployeeCode().equals(managerCode)) {
                throw new UserNotFoundException("Invalid employee for the manager");
            }
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new DateNotValidException("Start date must be before or equal to end date");
        }

        Specification<LeaveRequest> specification =
                LeaveRequestSpecification
                        .filterLeaveRequests(managerCode, status, employeeCode, startDate, endDate);

        return leaveRequestRepository
                .findAll(specification)
                .stream()
                .map(Mapper::mapLeaveRequestToLeaveRequestResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void applyLeave(String employeeCode, ApplyLeaveRequestDto leaveRequestDto)
            throws UserNotFoundException,
            DateNotValidException,
            LeaveBalanceNotValidException {

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

    @Override
    @Transactional
    public void updateLeaveStatus(
            String leaveRequestId,
            String managerCode,
            LeaveApprovalRequestDto leaveApprovalRequestDto
    ) throws LeaveRequestNotFoundException,
            AccessDeniedException,
            LeaveBalanceNotValidException,
            InvalidRequestException {

        if (leaveApprovalRequestDto.getStatus() != LeaveStatus.APPROVED &&
                leaveApprovalRequestDto.getStatus() != LeaveStatus.REJECTED) {
            throw new InvalidRequestException("Status must be APPROVED or REJECTED");
        }

        LeaveRequest leaveRequest =
                leaveRequestRepository
                        .findById(leaveRequestId)
                        .orElseThrow(() ->
                                new LeaveRequestNotFoundException("Leave Request not found"));

        if (!leaveRequest.getReportingManager().getEmployeeCode().equals(managerCode)) {
            throw new AccessDeniedException("You cannot process this request");
        }

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidRequestException("Only pending requests can be processed");
        }

        validateLeaveBalance(
                leaveRequest.getEmployee().getEmployeeCode(),
                leaveRequest.getLeaveType(),
                leaveRequest.getNumberOfDays());

        if (leaveApprovalRequestDto.getStatus() == LeaveStatus.APPROVED) {
            LeaveBalanceId leaveBalanceId = new LeaveBalanceId(
                            leaveRequest.getEmployee().getEmployeeCode(),
                            leaveRequest.getLeaveType());
            LeaveBalance leaveBalance =
                    leaveBalanceRepository
                            .findById(leaveBalanceId)
                            .orElseThrow(() ->
                                    new LeaveBalanceNotValidException("Leave balance not found"));
            leaveBalance.setLeavesUsed(
                    leaveBalance.getLeavesUsed() + leaveRequest.getNumberOfDays());

            leaveRequest.setStatus(LeaveStatus.APPROVED);
            leaveRequest.setManagerComments(leaveApprovalRequestDto.getManagerComments());

            leaveBalanceRepository.save(leaveBalance);
            leaveRequestRepository.save(leaveRequest);
        }

        if (leaveApprovalRequestDto.getStatus() == LeaveStatus.REJECTED) {
            leaveRequest.setStatus(LeaveStatus.REJECTED);
            leaveRequest.setManagerComments(leaveApprovalRequestDto.getManagerComments());
            leaveRequestRepository.save(leaveRequest);
        }
    }

    private void validateOverlappingLeaveRequests(
            String empCode,
            LocalDate startDate,
            LocalDate endDate) throws DateNotValidException {

        boolean exists =
                leaveRequestRepository.hasOverlappingLeaveRequests(empCode, startDate, endDate);

        if (exists) {
            throw new DateNotValidException("Overlapping leave request exists");
        }
    }

    private void validateLeaveBalance(
            String empCode,
            LeaveType leaveType,
            Integer daysRequested) throws LeaveBalanceNotValidException {

        LeaveBalanceId id = new LeaveBalanceId(empCode, leaveType);
        LeaveBalance leaveBalance =
                leaveBalanceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new LeaveBalanceNotValidException("Leave Balance not found"));

        if (leaveBalance.getRemainingLeaves() < daysRequested) {
            throw new LeaveBalanceNotValidException("Insufficient leave balance");
        }
    }
}
