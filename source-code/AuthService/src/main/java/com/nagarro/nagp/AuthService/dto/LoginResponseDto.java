package com.nagarro.nagp.AuthService.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponseDto {

    private String jwtToken;
    private String username;
    private List<String> roles;

    public LoginResponseDto(String username, List<String> roles, String jwtToken) {
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtToken;
    }

}
