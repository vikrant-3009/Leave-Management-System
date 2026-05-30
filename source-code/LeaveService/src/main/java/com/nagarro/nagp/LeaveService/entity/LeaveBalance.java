package com.nagarro.nagp.LeaveService.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "leave_balances")
@Getter
@Setter
public class LeaveBalance {

    @EmbeddedId
    private LeaveBalanceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "employee_code",
            referencedColumnName = "employee_code",
            insertable = false,
            updatable = false
    )
    private Employee employee;

    @Column(name = "accrued_leaves", nullable = false)
    private Integer accruedLeaves;

    @Column(name = "leaves_used", nullable = false)
    private Integer leavesUsed;

    @Transient
    public Integer getRemainingLeaves() {
        return accruedLeaves - leavesUsed;
    }
}
