package com.nagarro.nagp.LeaveService.service.impl;

import com.nagarro.nagp.LeaveService.exception.UserNotFoundException;
import com.nagarro.nagp.LeaveService.filter.AuthenticatedUser;
import com.nagarro.nagp.LeaveService.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthorizationService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public boolean canAccessEmployeeData(Authentication auth, String empCodeToAccess) {
        AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("Authenticated user not found");
        }
        String loggedInEmpCode = user.getEmployeeCode();

        // Emp can access own data
        if (loggedInEmpCode.equals(empCodeToAccess)) {
            return true;
        }

        // Manager can access team's employee data
        return employeeRepository
                .existsByEmployeeCodeAndManagerEmployeeCode(empCodeToAccess, loggedInEmpCode);
    }
}
