package com.employee.identity.controller;

import com.employee.identity.dto.request.CreateUserRequest;
import com.employee.identity.dto.response.UserResponse;
import com.employee.identity.exception.DuplicateUserException;
import com.employee.identity.exception.GlobalExceptionHandler;
import com.employee.identity.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    private UserService userService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        userService = mock(UserService.class);

        UserController userController =
                new UserController(userService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldCreateUser() throws Exception {

        CreateUserRequest request =
                new CreateUserRequest(
                        "employee@company.com",
                        "Employee@123"
                );

        UserResponse response =
                new UserResponse(
                        1L,
                        "employee@company.com",
                        true,
                        false,
                        Set.of("USER"),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(userService.createUser(any(CreateUserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/identity/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id").value(1)
                )
                .andExpect(
                        jsonPath("$.email")
                                .value("employee@company.com")
                )
                .andExpect(
                        jsonPath("$.enabled").value(true)
                )
                .andExpect(
                        jsonPath("$.accountLocked").value(false)
                )
                .andExpect(
                        jsonPath("$.roles[0]").value("USER")
                );

        verify(userService)
                .createUser(any(CreateUserRequest.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidRequest()
            throws Exception {

        CreateUserRequest request =
                new CreateUserRequest(
                        "invalid-email",
                        "123"
                );

        mockMvc.perform(
                        post("/api/identity/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status").value(400)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Request validation failed")
                )
                .andExpect(
                        jsonPath("$.validationErrors.email")
                                .value("Email must be valid")
                )
                .andExpect(
                        jsonPath("$.validationErrors.password")
                                .value(
                                        "Password must be between 8 and 72 characters"
                                )
                );

        verifyNoInteractions(userService);
    }

    @Test
    void shouldReturnConflictWhenUserAlreadyExists()
            throws Exception {

        CreateUserRequest request =
                new CreateUserRequest(
                        "employee@company.com",
                        "Employee@123"
                );

        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(
                        new DuplicateUserException(
                                "employee@company.com"
                        )
                );

        mockMvc.perform(
                        post("/api/identity/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(
                        jsonPath("$.status").value(409)
                )
                .andExpect(
                        jsonPath("$.error").value("Conflict")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "User already exists with email: employee@company.com"
                                )
                )
                .andExpect(
                        jsonPath("$.path")
                                .value("/api/identity/users")
                );

        verify(userService)
                .createUser(any(CreateUserRequest.class));
    }
}