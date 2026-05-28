package com.nagarro.nagp.LeaveService.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {

    private String username;
    private String employeeCode;
}
