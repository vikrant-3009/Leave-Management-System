package com.nagarro.nagp.AuthService.exception;

public class JwtTokenException extends RuntimeException {

    public JwtTokenException() {
        super("Invalid JWT token");
    }

    public JwtTokenException(String message) {
        super(message);
    }
}
