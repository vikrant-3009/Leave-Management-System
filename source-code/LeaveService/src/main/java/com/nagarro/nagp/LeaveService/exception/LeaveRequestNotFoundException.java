package com.nagarro.nagp.LeaveService.exception;

public class LeaveRequestNotFoundException extends RuntimeException {

    public LeaveRequestNotFoundException() {
        super("Leave request not found");
    }

    public LeaveRequestNotFoundException(String message) {
        super(message);
    }
}
