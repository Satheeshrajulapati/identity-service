package com.employee.identity.service;

import com.employee.identity.dto.request.LoginRequest;
import com.employee.identity.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}