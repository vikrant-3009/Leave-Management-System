package com.nagarro.nagp.AuthService.exception;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<String> handleMalformedJwtException(MalformedJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
    }

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<String> handleJwtTokenException(JwtTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error with JWT token: " + ex.getMessage());
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<String> handleUnsupportedJwtException(UnsupportedJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token is unsupported: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("JWT claims string is empty: " + ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "Login Failed: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(map);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
    }
}
