package com.employee.identity.service.impl;

import com.employee.identity.dto.request.LoginRequest;
import com.employee.identity.dto.response.LoginResponse;
import com.employee.identity.security.CustomUserDetails;
import com.employee.identity.security.JwtService;
import com.employee.identity.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager =
                authenticationManager;

        this.jwtService =
                jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email().trim(),
                                request.password()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails)
                        authentication.getPrincipal();

        Set<String> authorities =
                userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());

        Set<String> roles =
                authorities.stream()
                        .filter(authority ->
                                authority.startsWith("ROLE_")
                        )
                        .map(authority ->
                                authority.substring(5)
                        )
                        .collect(Collectors.toSet());

        Set<String> permissions =
                authorities.stream()
                        .filter(authority ->
                                !authority.startsWith("ROLE_")
                        )
                        .collect(Collectors.toSet());

        String accessToken =
                jwtService.generateToken(userDetails);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getAccessTokenExpiration(),
                userDetails.getUsername(),
                roles,
                permissions
        );
    }
}