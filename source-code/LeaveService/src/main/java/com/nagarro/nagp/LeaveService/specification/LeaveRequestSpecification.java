package com.nagarro.nagp.LeaveService.specification;

import com.nagarro.nagp.LeaveService.entity.LeaveRequest;
import com.nagarro.nagp.LeaveService.enums.LeaveStatus;
import com.nagarro.nagp.LeaveService.enums.LeaveType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestSpecification {

    public static Specification<LeaveRequest> filterLeaveRequests(
            String managerCode,
            LeaveStatus status,
            String employeeCode,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (leaveRequest, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Predicate p1 = cb.equal(
                    leaveRequest.get("reportingManager").get("employeeCode"),
                    managerCode
            );
            predicates.add(p1);

            if (status != null) {
                Predicate p2 = cb.equal(leaveRequest.get("status"), status);
                predicates.add(p2);
            }

            if (employeeCode != null && !employeeCode.isBlank()) {
                Predicate p3 = cb.equal(
                        leaveRequest.get("employee").get("employeeCode"),
                        employeeCode
                );
                predicates.add(p3);
            }

            if (startDate != null) {
                Predicate p4 = cb.greaterThanOrEqualTo(
                        leaveRequest.get("startDate"),
                        startDate
                );
                predicates.add(p4);
            }

            if (endDate != null) {
                Predicate p5 = cb.lessThanOrEqualTo(
                        leaveRequest.get("endDate"),
                        endDate
                );
                predicates.add(p5);
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<LeaveRequest> employeeLeaveRequestHistory(
            String employeeCode,
            LeaveStatus status,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (leaveRequest, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Predicate p1 = cb.equal(
                    leaveRequest.get("employee").get("employeeCode"),
                    employeeCode
            );
            predicates.add(p1);

            if (status != null) {
                Predicate p2 = cb.equal(leaveRequest.get("status"), status);
                predicates.add(p2);
            }

            if (leaveType != null) {
                Predicate p3 = cb.equal(leaveRequest.get("leaveType"), leaveType);
                predicates.add(p3);
            }

            if (startDate != null) {
                Predicate p4 = cb.greaterThanOrEqualTo(
                        leaveRequest.get("startDate"),
                        startDate
                );
                predicates.add(p4);
            }

            if (endDate != null) {
                Predicate p5 = cb.lessThanOrEqualTo(
                        leaveRequest.get("endDate"),
                        endDate
                );
                predicates.add(p5);
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
