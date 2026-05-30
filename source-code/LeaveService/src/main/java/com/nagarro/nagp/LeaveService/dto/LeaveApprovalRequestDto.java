package com.nagarro.nagp.LeaveService.dto;

import com.nagarro.nagp.LeaveService.enums.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveApprovalRequestDto {

    @NotNull(message = "status is required")
    private LeaveStatus status; // APPROVED or REJECTED

    private String managerComments;
}
