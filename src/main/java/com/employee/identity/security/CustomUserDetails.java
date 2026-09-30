package com.employee.identity.security;

import com.employee.identity.entity.Permission;
import com.employee.identity.entity.Role;
import com.employee.identity.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Role role : user.getRoles()) {

            if (!Boolean.TRUE.equals(role.getActive())) {
                continue;
            }

            authorities.add(
                    new SimpleGrantedAuthority(
                            "ROLE_" + role.getCode()
                    )
            );

            for (Permission permission : role.getPermissions()) {

                if (Boolean.TRUE.equals(permission.getActive())) {
                    authorities.add(
                            new SimpleGrantedAuthority(
                                    permission.getCode()
                            )
                    );
                }
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !Boolean.TRUE.equals(user.getAccountLocked());
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(user.getEnabled());
    }
}