package com.nagarro.nagp.LeaveService.dto;

import com.nagarro.nagp.LeaveService.enums.LeaveStatus;
import com.nagarro.nagp.LeaveService.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequestResponseDto {

    private String leaveRequestId;

    private String employeeCode;

    private String employeeName;

    private LeaveType leaveType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer numberOfDays;

    private String reason;

    private LeaveStatus status;

    private String reportingManagerCode;

    private String reportingManagerName;

    private LocalDateTime lastModifiedAt;

    private String lastModifiedBy;
}
