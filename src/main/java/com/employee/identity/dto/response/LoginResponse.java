package com.employee.identity.dto.response;

import java.util.Set;

public record LoginResponse(

        String accessToken,
        String tokenType,
        long expiresIn,

        String email,

        Set<String> roles,
        Set<String> permissions

) {
}