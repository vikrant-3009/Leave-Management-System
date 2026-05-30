package com.nagarro.nagp.LeaveService.service.impl;

import com.nagarro.nagp.LeaveService.dto.LeaveBalanceResponseDto;
import com.nagarro.nagp.LeaveService.entity.Employee;
import com.nagarro.nagp.LeaveService.entity.LeaveBalance;
import com.nagarro.nagp.LeaveService.entity.LeaveBalanceId;
import com.nagarro.nagp.LeaveService.enums.LeaveType;
import com.nagarro.nagp.LeaveService.exception.UserNotFoundException;
import com.nagarro.nagp.LeaveService.repository.EmployeeRepository;
import com.nagarro.nagp.LeaveService.repository.LeaveBalanceRepository;
import com.nagarro.nagp.LeaveService.service.LeaveBalanceService;
import com.nagarro.nagp.LeaveService.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponseDto> getEmployeeLeaveBalances(String empCode)
            throws UserNotFoundException {
        List<LeaveBalance> balances = leaveBalanceRepository.findByIdEmployeeCode(empCode);

        if (balances.isEmpty()) {
            throw new UserNotFoundException();
        }
        return balances.stream()
                .map(balance -> LeaveBalanceResponseDto.builder()
                        .employeeCode(balance.getId().getEmployeeCode())
                        .leaveType(balance.getId().getLeaveType())
                        .accruedLeaves(balance.getAccruedLeaves())
                        .leavesUsed(balance.getLeavesUsed())
                        .remainingLeaves(balance.getRemainingLeaves())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponseDto> getTeamLeaveBalances(String managerCode) {
        List<Employee> employees = employeeRepository.findByManagerEmployeeCode(managerCode);
        List<String> employeeCodes = employees.stream().map(Employee::getEmployeeCode).toList();
        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findByIdEmployeeCodeIn(employeeCodes);
        return leaveBalances.stream().map(Mapper::mapLeaveBalToLeaveBalResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveBalanceResponseDto getEmployeeLeaveBalanceByType(String empCode, LeaveType leaveType)
            throws UserNotFoundException {
        LeaveBalanceId leaveBalanceId = new LeaveBalanceId(empCode, leaveType);
        LeaveBalance leaveBalance =
                leaveBalanceRepository
                        .findById(leaveBalanceId)
                        .orElseThrow(() -> new UserNotFoundException("Leave Balance not found"));
        return Mapper.mapLeaveBalToLeaveBalResponseDto(leaveBalance);
    }
}
