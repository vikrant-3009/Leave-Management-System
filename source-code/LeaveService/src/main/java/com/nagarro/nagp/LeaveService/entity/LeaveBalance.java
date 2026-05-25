package com.nagarro.nagp.LeaveService.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "leave_balances")
@Data
public class LeaveBalance {

    @EmbeddedId
    private LeaveBalanceId id;

    @Column(name = "accrued_leaves", nullable = false)
    private Integer accruedLeaves;

    @Column(name = "leaves_used", nullable = false)
    private Integer leavesUsed;

    @Transient
    public Integer getRemainingLeaves() {
        return accruedLeaves - leavesUsed;
    }
}
