package com.nagarro.nagp.LeaveService.entity;

import com.nagarro.nagp.LeaveService.enums.LeaveType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "leave_policies")
@Getter
@Setter
public class LeavePolicy {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type")
    private LeaveType leaveType;

    @Column(name = "yearly_quota", nullable = false)
    private Integer yearlyQuota;

    @Column(name = "max_carry_forward_allowed", nullable = false)
    private Integer maxCarryForwardAllowed;
}
