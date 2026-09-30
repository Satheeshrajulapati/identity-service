package com.employee.identity.service;

import com.employee.identity.dto.request.CreateUserRequest;
import com.employee.identity.dto.response.UserResponse;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);
}