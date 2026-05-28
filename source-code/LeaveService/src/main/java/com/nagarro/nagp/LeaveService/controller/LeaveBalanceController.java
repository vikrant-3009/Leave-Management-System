package com.nagarro.nagp.LeaveService.controller;

import com.nagarro.nagp.LeaveService.dto.LeaveBalanceResponseDto;
import com.nagarro.nagp.LeaveService.enums.LeaveType;
import com.nagarro.nagp.LeaveService.exception.UserNotFoundException;
import com.nagarro.nagp.LeaveService.filter.AuthenticatedUser;
import com.nagarro.nagp.LeaveService.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestController
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    /**
     * Retrieves the leave balances for the currently authenticated user.
     *
     * <p>This endpoint extracts the authenticated user's details from the Spring Security
     * {@link Authentication} object and uses the employee code associated with the
     * logged-in user to fetch their leave balances.</p>
     *
     * @param authentication the Spring Security authentication object containing the current user's details
     * @return a list of {@link LeaveBalanceResponseDto} representing the logged-in user's leave balances
     * @throws UserNotFoundException if the authenticated user is null or cannot be resolved
     */
    @GetMapping("/balances/me")
    public List<LeaveBalanceResponseDto> getMyBalances(Authentication authentication)
            throws UserNotFoundException {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return leaveBalanceService.getEmployeeLeaveBalances(user.getEmployeeCode());
    }

    /**
     * Retrieves the leave balances of a specific type for a given employee.
     *
     * <p>This endpoint is secured using {@link PreAuthorize} to ensure that only
     * authorized users can access the requested employee's data. The authorization
     * logic is delegated to {@code authorizationService.canAccessEmployeeData()}.</p>
     *
     * @param employeeCode the unique identifier of the employee whose leave balances are to be fetched
     * @param leaveType the type of leave for which the balance is requested (passed as a request parameter)
     * @return a {@link LeaveBalanceResponseDto} representing the employee's leave balances
     * representing the leave balance for the specified type
     * @throws UserNotFoundException if the employee corresponding to the given employeeCode does not exist
     */
    @PreAuthorize(
            "@authorizationService.canAccessEmployeeData(authentication, #employeeCode)"
    )
    @GetMapping("/balances/{employeeCode}/type")
    public LeaveBalanceResponseDto getLeaveBalanceByType(
            @PathVariable String employeeCode,
            @RequestParam LeaveType leaveType)
            throws UserNotFoundException, MethodArgumentTypeMismatchException {
        return leaveBalanceService.getEmployeeLeaveBalanceByType(employeeCode, leaveType);
    }

    /**
     * Retrieves the leave balances for a specific employee.
     *
     * <p>This endpoint is secured using {@link PreAuthorize} to ensure that only
     * authorized users can access the requested employee's data. The authorization
     * logic is delegated to {@code authorizationService.canAccessEmployeeData()}.</p>
     *
     * @param employeeCode the unique identifier of the employee whose leave balances are to be fetched
     * @return a list of {@link LeaveBalanceResponseDto} representing the employee's leave balances
     * @throws UserNotFoundException if the employee corresponding to the given employeeCode does not exist
     */
    @PreAuthorize(
            "@authorizationService.canAccessEmployeeData(authentication, #employeeCode)"
    )
    @GetMapping("/balances/{employeeCode}")
    public List<LeaveBalanceResponseDto> getBalancesForEmployee(
            @PathVariable String employeeCode) throws UserNotFoundException {
        return leaveBalanceService.getEmployeeLeaveBalances(employeeCode);
    }

    /**
     * Retrieves the leave balances of all team members reporting to the authenticated manager.
     *
     * <p>This endpoint is restricted to users with the {@code MANAGER} role.
     * It extracts the manager's employee code from the {@link Authentication} object
     * and fetches the leave balances of employees who report to that manager.</p>
     *
     * @param auth the Spring Security authentication object containing the current user's details
     * @return a list of {@link LeaveBalanceResponseDto} representing the leave balances of the manager's team members
     * @throws UserNotFoundException if the authenticated user is null or cannot be resolved as a valid manager
     */
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/balances/team")
    public List<LeaveBalanceResponseDto> getTeamLeaveBalances(Authentication auth)
            throws UserNotFoundException {
        AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
        if (user == null) {
            throw new UserNotFoundException("Manager not found");
        }
        String managerCode = user.getEmployeeCode();
        return leaveBalanceService.getTeamLeaveBalances(managerCode);
    }
}
