package com.employee.identity.security;

import com.employee.identity.entity.User;
import com.employee.identity.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(
            String email
    ) throws UsernameNotFoundException {

        User user = userRepository
                .findWithRolesAndPermissionsByEmailIgnoreCase(
                        email.trim()
                )
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "Invalid email or password"
                        )
                );

        return new CustomUserDetails(user);
    }
}