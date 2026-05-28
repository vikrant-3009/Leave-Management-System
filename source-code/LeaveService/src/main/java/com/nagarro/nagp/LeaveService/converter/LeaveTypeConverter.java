package com.nagarro.nagp.LeaveService.converter;

import com.nagarro.nagp.LeaveService.enums.LeaveType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LeaveTypeConverter implements Converter<String, LeaveType> {

    @Override
    public LeaveType convert(String source) {
        try {
            return LeaveType.valueOf(source.toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid leave type");
        }
    }
}
