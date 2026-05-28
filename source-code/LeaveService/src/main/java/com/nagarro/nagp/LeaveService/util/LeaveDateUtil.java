package com.nagarro.nagp.LeaveService.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class LeaveDateUtil {

    public static int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        int workingDays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workingDays += 1;
            }
            currentDate = currentDate.plusDays(1);
        }
        return workingDays;
    }
}
