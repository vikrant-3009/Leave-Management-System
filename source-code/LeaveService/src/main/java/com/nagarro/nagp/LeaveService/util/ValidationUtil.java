package com.nagarro.nagp.LeaveService.util;

import com.nagarro.nagp.LeaveService.exception.DateNotValidException;
import com.nagarro.nagp.LeaveService.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class ValidationUtil {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    public static void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isBefore(LocalDate.now())) {
            throw new DateNotValidException("Past dates are not allowed");
        }
        if (startDate.isAfter(endDate)) {
            throw new DateNotValidException("Start date must be before or equal to end date");
        }
    }
}
