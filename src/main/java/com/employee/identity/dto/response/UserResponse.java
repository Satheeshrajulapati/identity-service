package com.employee.identity.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(

        Long id,
        String email,
        Boolean enabled,
        Boolean accountLocked,
        Set<String> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}