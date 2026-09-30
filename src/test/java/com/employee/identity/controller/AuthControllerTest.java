package com.employee.identity.controller;

import com.employee.identity.dto.request.LoginRequest;
import com.employee.identity.dto.response.LoginResponse;
import com.employee.identity.exception.GlobalExceptionHandler;
import com.employee.identity.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;

    private AuthService authService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        authService = mock(AuthService.class);

        AuthController authController =
                new AuthController(authService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(
                        new GlobalExceptionHandler()
                )
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {

        LoginRequest request =
                new LoginRequest(
                        "employee@company.com",
                        "Employee@123"
                );

        LoginResponse response =
                new LoginResponse(
                        "test-access-token",
                        "Bearer",
                        900L,
                        "employee@company.com",
                        Set.of("USER"),
                        Set.of(
                                "EMPLOYEE_READ",
                                "ORGANIZATION_READ"
                        )
                );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/identity/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())

                .andExpect(
                        jsonPath("$.accessToken")
                                .value("test-access-token")
                )

                .andExpect(
                        jsonPath("$.tokenType")
                                .value("Bearer")
                )

                .andExpect(
                        jsonPath("$.expiresIn")
                                .value(900)
                )

                .andExpect(
                        jsonPath("$.email")
                                .value("employee@company.com")
                )

                .andExpect(
                        jsonPath("$.roles")
                                .isArray()
                )

                .andExpect(
                        jsonPath("$.roles[0]")
                                .value("USER")
                )

                .andExpect(
                        jsonPath("$.permissions")
                                .isArray()
                )

                .andExpect(
                        jsonPath("$.permissions.length()")
                                .value(2)
                );

        verify(authService)
                .login(any(LoginRequest.class));
    }

    @Test
    void shouldReturnUnauthorizedForInvalidCredentials()
            throws Exception {

        LoginRequest request =
                new LoginRequest(
                        "employee@company.com",
                        "WrongPassword"
                );

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(
                        new BadCredentialsException(
                                "Bad credentials"
                        )
                );

        mockMvc.perform(
                        post("/api/identity/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isUnauthorized())

                .andExpect(
                        jsonPath("$.status")
                                .value(401)
                )

                .andExpect(
                        jsonPath("$.error")
                                .value("Unauthorized")
                )

                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Invalid email or password"
                                )
                )

                .andExpect(
                        jsonPath("$.path")
                                .value(
                                        "/api/identity/auth/login"
                                )
                );

        verify(authService)
                .login(any(LoginRequest.class));
    }

    @Test
    void shouldReturnBadRequestForInvalidLoginRequest()
            throws Exception {

        LoginRequest request =
                new LoginRequest(
                        "invalid-email",
                        ""
                );

        mockMvc.perform(
                        post("/api/identity/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest())

                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )

                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Request validation failed"
                                )
                )

                .andExpect(
                        jsonPath("$.validationErrors.email")
                                .value(
                                        "Email must be valid"
                                )
                )

                .andExpect(
                        jsonPath("$.validationErrors.password")
                                .value(
                                        "Password is required"
                                )
                );

        verifyNoInteractions(authService);
    }
}