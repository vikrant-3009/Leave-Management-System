package com.nagarro.nagp.LeaveService.controller;

import com.nagarro.nagp.LeaveService.dto.LeaveBalanceResponseDto;
import com.nagarro.nagp.LeaveService.exception.UserNotFoundException;
import com.nagarro.nagp.LeaveService.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @GetMapping("/balances/{employeeCode}")
//    @PreAuthorize(
//            "#employeeCode == authentication.name || hasRole('MANAGER')"
//    )
    public List<LeaveBalanceResponseDto> getBalances(
            @PathVariable String employeeCode
    ) throws UserNotFoundException {
        return leaveBalanceService.getEmployeeLeaveBalances(employeeCode);
    }
}
