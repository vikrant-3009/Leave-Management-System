package com.nagarro.nagp.API_Gateway.exception;


public class AuthenticationException extends RuntimeException {

    public AuthenticationException() {
        super("Authentication Error");
    }

    public AuthenticationException(String message) {
        super(message);
    }
}
