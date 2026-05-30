package com.nagarro.nagp.LeaveService.controller;

import com.nagarro.nagp.LeaveService.dto.ApplyLeaveRequestDto;
import com.nagarro.nagp.LeaveService.dto.LeaveApprovalRequestDto;
import com.nagarro.nagp.LeaveService.dto.LeaveRequestResponseDto;
import com.nagarro.nagp.LeaveService.enums.LeaveStatus;
import com.nagarro.nagp.LeaveService.enums.LeaveType;
import com.nagarro.nagp.LeaveService.exception.DateNotValidException;
import com.nagarro.nagp.LeaveService.exception.LeaveBalanceNotValidException;
import com.nagarro.nagp.LeaveService.exception.UserNotFoundException;
import com.nagarro.nagp.LeaveService.filter.AuthenticatedUser;
import com.nagarro.nagp.LeaveService.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/request")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestController.class);

    @GetMapping("/history")
    public Page<LeaveRequestResponseDto> getLeaveRequestHistory(
            Authentication authentication,
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) LeaveType leaveType,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "lastModifiedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) throws UserNotFoundException, DateNotValidException {

        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        logger.info("Fetching Leave requests for: {}", user.getEmployeeCode());
        return leaveRequestService.getLeaveRequestHistory(
                user.getEmployeeCode(),
                status,
                leaveType,
                startDate,
                endDate,
                page,
                size,
                sortBy,
                direction);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/team")
    public List<LeaveRequestResponseDto> getTeamLeaveRequests(
            Authentication authentication,
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String employeeCode
    ) throws UserNotFoundException, DateNotValidException {

        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        if (user == null) {
            throw new UserNotFoundException("Manager not found");
        }
        logger.info("Fetching Team Leave requests for: {}", user.getEmployeeCode());
        return leaveRequestService.getTeamLeaveRequests(
                user.getEmployeeCode(),
                status,
                employeeCode,
                startDate,
                endDate);
    }

    @PreAuthorize("#employeeCode == authentication.principal.employeeCode")
    @PostMapping("/apply/{employeeCode}")
    public ResponseEntity<?> applyLeave(
            @PathVariable String employeeCode,
            @RequestBody ApplyLeaveRequestDto leaveRequest
    ) throws LeaveBalanceNotValidException,
            UserNotFoundException,
            DateNotValidException {

        logger.info("Applying Leave for: {}", employeeCode);
        leaveRequestService.applyLeave(employeeCode, leaveRequest);

        logger.info("Leave applied successfully");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Leave applied successfully");
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/update")
    public ResponseEntity<?> updateLeaveStatus(
            Authentication authentication,
            @RequestParam String leaveRequestId,
            @Valid @RequestBody LeaveApprovalRequestDto leaveApprovalRequestDto
    ) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        logger.info("Updating Leave Request");
        leaveRequestService.updateLeaveStatus(
                leaveRequestId,
                user.getEmployeeCode(),
                leaveApprovalRequestDto
        );

        logger.info("Leave Request updated successfully");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Leave Request updated successfully");
    }
}
