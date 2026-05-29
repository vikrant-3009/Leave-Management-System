package com.nagarro.nagp.LeaveService.controller;

import com.nagarro.nagp.LeaveService.dto.ApplyLeaveRequestDto;
import com.nagarro.nagp.LeaveService.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PreAuthorize("#employeeCode == authentication.principal.employeeCode")
    @PostMapping("/apply/{employeeCode}")
    public ResponseEntity<?> applyLeave(
            @PathVariable String employeeCode,
            @RequestBody ApplyLeaveRequestDto leaveRequest) {

        leaveRequestService.applyLeave(employeeCode, leaveRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Leave applied successfully");
    }
}
