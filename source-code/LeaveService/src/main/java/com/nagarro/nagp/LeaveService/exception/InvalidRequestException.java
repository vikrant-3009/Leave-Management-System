package com.nagarro.nagp.LeaveService.exception;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException() {
        super("Invalid Request");
    }

    public InvalidRequestException(String message) {
        super(message);
    }
}
