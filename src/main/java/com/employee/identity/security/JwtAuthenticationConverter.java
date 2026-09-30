package com.employee.identity.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class JwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<GrantedAuthority> authorities =
                new ArrayList<>();

        List<String> roles =
                jwt.getClaimAsStringList("roles");

        if (roles != null) {
            roles.stream()
                    .map(role ->
                            new SimpleGrantedAuthority(
                                    "ROLE_" + role
                            )
                    )
                    .forEach(authorities::add);
        }

        List<String> permissions =
                jwt.getClaimAsStringList("permissions");

        if (permissions != null) {
            permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                jwt.getSubject()
        );
    }
}