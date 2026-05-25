package com.nagarro.nagp.LeaveService.entity;

import com.nagarro.nagp.LeaveService.enums.LeaveType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Embeddable
public class LeaveBalanceId implements Serializable {

    @Column(name = "employee_code", nullable = false)
    private String employeeCode;

    @Column(name = "leave_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    public LeaveBalanceId() {
    }

    public LeaveBalanceId(String employeeCode, LeaveType leaveType) {
        this.employeeCode = employeeCode;
        this.leaveType = leaveType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaveBalanceId that = (LeaveBalanceId) o;
        return Objects.equals(employeeCode, that.employeeCode)
                && Objects.equals(leaveType, that.leaveType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeCode, leaveType);
    }
}
