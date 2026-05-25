package com.nagarro.nagp.AuthService.controller;

import com.nagarro.nagp.AuthService.dto.LoginRequestDto;
import com.nagarro.nagp.AuthService.dto.LoginResponseDto;
import com.nagarro.nagp.AuthService.exception.UserNotFoundException;
import com.nagarro.nagp.AuthService.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/employee")
    public String userEndpoint() {
        return "Hello, Employee!";
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/manager")
    public String adminEndpoint() {
        return "Hello, Manager!";
    }

//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
//    @GetMapping("/leaves/{employeeId}")
//    public List<LeaveDto> getLeaves(
//            @PathVariable String employeeId) {
//        return null;
//    }
//
//    @PreAuthorize("hasRole('MANAGER')")
//    @GetMapping("/team-leaves")
//    public List<LeaveDto> getTeamLeaves() {
//        return leaveService.getTeamLeaves();
//    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDto loginRequest)
            throws AuthenticationException, UserNotFoundException {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//        try {
//            authentication = authenticationManager
//                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//        } catch (AuthenticationException exception) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("message", "Bad credentials");
//            map.put("status", false);
//            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
//        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = null;
        List<String> roles = null;
        LoginResponseDto response = null;

        if (userDetails != null) {
            jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
            roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            response = new LoginResponseDto(userDetails.getUsername(), roles, jwtToken);
        } else {
            throw new UserNotFoundException("userDetails is null");
        }

        return ResponseEntity.ok(response);
    }
}
