package com.nagarro.nagp.LeaveService.repository;

import com.nagarro.nagp.LeaveService.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String> {

    @Query("""
            SELECT COUNT(l) > 0
            FROM LeaveRequest l
            WHERE l.employee.employeeCode = :empCode
            AND l.status != 'REJECTED'
            AND (
                :startDate <= l.endDate
                AND
                :endDate >= l.startDate
            )
            """)
    boolean hasOverlappingLeaveRequests(
            @Param("empCode") String empCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
