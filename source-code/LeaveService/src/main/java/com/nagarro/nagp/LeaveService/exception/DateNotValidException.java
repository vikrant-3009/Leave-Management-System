package com.nagarro.nagp.LeaveService.exception;

public class DateNotValidException extends RuntimeException {

    public DateNotValidException() {
        super("Invalid date");
    }

    public DateNotValidException(String message) {
        super(message);
    }
}