package com.employee.identity.security;

import com.employee.identity.config.JwtProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;

    public JwtService(
            JwtEncoder jwtEncoder,
            JwtProperties jwtProperties
    ) {
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(
            CustomUserDetails userDetails
    ) {

        Instant now = Instant.now();

        Instant expiresAt =
                now.plusSeconds(
                        jwtProperties.accessTokenExpiration()
                );

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

        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .issuer(jwtProperties.issuer())
                        .subject(userDetails.getUsername())
                        .issuedAt(now)
                        .expiresAt(expiresAt)
                        .claim("roles", roles)
                        .claim(
                                "permissions",
                                permissions
                        )
                        .build();

        return jwtEncoder
                .encode(
                        JwtEncoderParameters.from(claims)
                )
                .getTokenValue();
    }

    public long getAccessTokenExpiration() {
        return jwtProperties.accessTokenExpiration();
    }
}