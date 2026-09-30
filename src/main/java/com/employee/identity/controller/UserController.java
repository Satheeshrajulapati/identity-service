package com.employee.identity.controller;

import com.employee.identity.dto.request.CreateUserRequest;
import com.employee.identity.dto.response.UserResponse;
import com.employee.identity.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/identity/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        UserResponse response =
                userService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}