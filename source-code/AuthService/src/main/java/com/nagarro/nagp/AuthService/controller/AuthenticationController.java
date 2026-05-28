package com.nagarro.nagp.AuthService.controller;

import com.nagarro.nagp.AuthService.dto.LoginRequestDto;
import com.nagarro.nagp.AuthService.dto.LoginResponseDto;
import com.nagarro.nagp.AuthService.entity.CustomUserDetails;
import com.nagarro.nagp.AuthService.exception.JwtTokenException;
import com.nagarro.nagp.AuthService.exception.UserNotFoundException;
import com.nagarro.nagp.AuthService.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AuthenticationController {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from Auth Service...";
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam("token") String token)
            throws JwtTokenException {
        if (token == null) {
            throw new JwtTokenException("JWT token is required");
        }
        boolean isJwtValid = jwtUtils.validateJwtToken(token);

        if (!isJwtValid) {
            throw new JwtTokenException("JWT token is invalid");
        }
        String username = jwtUtils.getUserNameFromJwtToken(token);
        List<String> roles = jwtUtils.getRolesFromJwtToken(token);
        String employeeCode = jwtUtils.getEmployeeCodeFromJwtToken(token);
        Map<String, Object> response = new HashMap<>();

        response.put("username", username);
        response.put("roles", roles);
        response.put("employeeCode", employeeCode);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDto loginRequest)
            throws AuthenticationException, UserNotFoundException {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String jwtToken = null;
        List<String> roles = null;
        LoginResponseDto response = null;

        if (userDetails != null) {
            jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
            roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            response = new LoginResponseDto(
                    userDetails.getUsername(),
                    userDetails.getEmployeeCode(),
                    roles,
                    jwtToken
            );
        } else {
            throw new UserNotFoundException("userDetails is null");
        }

        return ResponseEntity.ok(response);
    }
}
