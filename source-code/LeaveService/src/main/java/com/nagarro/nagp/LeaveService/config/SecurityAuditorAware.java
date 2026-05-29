package com.nagarro.nagp.LeaveService.config;

import com.nagarro.nagp.LeaveService.exception.UserNotFoundException;
import com.nagarro.nagp.LeaveService.filter.AuthenticatedUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        String employeeCode = null;

        if (user != null) {
            employeeCode = user.getEmployeeCode();
        } else {
            throw new UserNotFoundException("User not found");
        }

        return Optional.of(employeeCode);
    }
}
