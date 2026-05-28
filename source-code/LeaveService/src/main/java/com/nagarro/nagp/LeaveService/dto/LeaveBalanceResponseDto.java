package com.nagarro.nagp.LeaveService.dto;

import com.nagarro.nagp.LeaveService.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceResponseDto {

    private String employeeCode;

    private LeaveType leaveType;

    private Integer accruedLeaves;

    private Integer leavesUsed;

    private Integer remainingLeaves;
}
