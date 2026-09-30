package com.employee.identity.exception;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String code) {
        super("Role not found with code: " + code);
    }
}