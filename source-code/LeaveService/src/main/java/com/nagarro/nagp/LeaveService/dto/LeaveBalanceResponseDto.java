package com.nagarro.nagp.LeaveService.dto;

import com.nagarro.nagp.LeaveService.enums.LeaveType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LeaveBalanceResponseDto {

    private LeaveType leaveType;

    private Integer accruedLeaves;

    private Integer leavesUsed;

    private Integer remainingLeaves;
}
