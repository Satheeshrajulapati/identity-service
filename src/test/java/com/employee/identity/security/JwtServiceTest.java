package com.employee.identity.security;

import com.employee.identity.config.JwtConfig;
import com.employee.identity.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(
        properties = {
                "jwt.private-key=classpath:keys/private.pem",
                "jwt.public-key=classpath:keys/public.pem",
                "jwt.issuer=https://identity.employee-management.local",
                "jwt.access-token-expiration=900"
        }
)
@EnableConfigurationProperties(JwtProperties.class)
@Import({
        JwtConfig.class,
        com.employee.identity.security.JwtService.class
})
class JwtServiceTest {

    @Autowired
    private com.employee.identity.security.JwtService jwtService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    void shouldGenerateAndVerifyJwtToken() {

        CustomUserDetails userDetails =
                mock(CustomUserDetails.class);

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

        String token =
                jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());

        Jwt jwt =
                jwtDecoder.decode(token);

        assertEquals(
                "employee@company.com",
                jwt.getSubject()
        );

        assertEquals(
                "https://identity.employee-management.local",
                jwt.getIssuer().toString()
        );

        List<String> roles =
                jwt.getClaimAsStringList("roles");

        assertNotNull(roles);
        assertTrue(roles.contains("USER"));

        List<String> permissions =
                jwt.getClaimAsStringList("permissions");

        assertNotNull(permissions);

        assertTrue(
                permissions.contains("EMPLOYEE_READ")
        );

        assertTrue(
                permissions.contains("ORGANIZATION_READ")
        );

        assertNotNull(jwt.getIssuedAt());
        assertNotNull(jwt.getExpiresAt());

        assertTrue(
                jwt.getExpiresAt()
                        .isAfter(jwt.getIssuedAt())
        );
    }
}