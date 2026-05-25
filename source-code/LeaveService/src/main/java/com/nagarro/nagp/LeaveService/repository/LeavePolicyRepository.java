package com.nagarro.nagp.LeaveService.repository;

import com.nagarro.nagp.LeaveService.entity.LeavePolicy;
import com.nagarro.nagp.LeaveService.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeavePolicyRepository extends JpaRepository<LeavePolicy, LeaveType> {
}
