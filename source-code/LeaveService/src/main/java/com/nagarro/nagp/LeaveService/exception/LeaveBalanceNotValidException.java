package com.nagarro.nagp.LeaveService.exception;

public class LeaveBalanceNotValidException extends RuntimeException {

    public LeaveBalanceNotValidException() {
        super("Leave Balance not enough");
    }

    public LeaveBalanceNotValidException(String message) {
        super(message);
    }
}
