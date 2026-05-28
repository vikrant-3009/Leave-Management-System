package com.nagarro.nagp.LeaveService.repository;

import com.nagarro.nagp.LeaveService.entity.LeaveBalance;
import com.nagarro.nagp.LeaveService.entity.LeaveBalanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, LeaveBalanceId> {

    List<LeaveBalance> findByIdEmployeeCode(String employeeCode);

    List<LeaveBalance> findByIdEmployeeCodeIn(List<String> employeeCodes);
}
