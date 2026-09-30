package com.employee.identity.service.impl;

import com.employee.identity.dto.request.LoginRequest;
import com.employee.identity.dto.response.LoginResponse;
import com.employee.identity.security.CustomUserDetails;
import com.employee.identity.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    @Mock
    private JwtService jwtService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                authenticationManager,
                jwtService
        );
    }

    @Test
    void shouldLoginSuccessfullyAndGenerateAccessToken() {

        LoginRequest request =
                new LoginRequest(
                        " employee@company.com ",
                        "Employee@123"
                );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(userDetails.getUsername())
                .thenReturn("employee@company.com");

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("EMPLOYEE_READ"),
                new SimpleGrantedAuthority("ORGANIZATION_READ")
        );

        doReturn(authorities)
                .when(userDetails)
                .getAuthorities();

        when(jwtService.generateToken(userDetails))
                .thenReturn("test-access-token");

        when(jwtService.getAccessTokenExpiration())
                .thenReturn(900L);

        LoginResponse response =
                authService.login(request);

        assertNotNull(response);

        assertEquals(
                "test-access-token",
                response.accessToken()
        );

        assertEquals(
                "Bearer",
                response.tokenType()
        );

        assertEquals(
                900L,
                response.expiresIn()
        );

        assertEquals(
                "employee@company.com",
                response.email()
        );

        assertTrue(
                response.roles().contains("USER")
        );

        assertEquals(
                1,
                response.roles().size()
        );

        assertTrue(
                response.permissions()
                        .contains("EMPLOYEE_READ")
        );

        assertTrue(
                response.permissions()
                        .contains("ORGANIZATION_READ")
        );

        assertEquals(
                2,
                response.permissions().size()
        );

        verify(jwtService)
                .generateToken(userDetails);

        verify(jwtService)
                .getAccessTokenExpiration();
    }

    @Test
    void shouldPassNormalizedEmailAndPasswordToAuthenticationManager() {

        LoginRequest request =
                new LoginRequest(
                        " employee@company.com ",
                        "Employee@123"
                );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(userDetails.getUsername())
                .thenReturn("employee@company.com");

        doReturn(List.<GrantedAuthority>of())
                .when(userDetails)
                .getAuthorities();

        when(jwtService.generateToken(userDetails))
                .thenReturn("test-access-token");

        when(jwtService.getAccessTokenExpiration())
                .thenReturn(900L);

        authService.login(request);

        verify(authenticationManager)
                .authenticate(
                        argThat(auth ->
                                "employee@company.com"
                                        .equals(auth.getPrincipal())
                                        &&
                                        "Employee@123"
                                                .equals(auth.getCredentials())
                        )
                );
    }

    @Test
    void shouldNotGenerateTokenWhenAuthenticationFails() {

        LoginRequest request =
                new LoginRequest(
                        "employee@company.com",
                        "WrongPassword"
                );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new BadCredentialsException(
                        "Bad credentials"
                )
        );

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verifyNoInteractions(jwtService);
        verifyNoInteractions(authentication);
        verifyNoInteractions(userDetails);
    }
}