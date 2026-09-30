package com.employee.identity.service.impl;

import com.employee.identity.dto.request.CreateUserRequest;
import com.employee.identity.dto.response.UserResponse;
import com.employee.identity.entity.Role;
import com.employee.identity.entity.User;
import com.employee.identity.exception.DuplicateUserException;
import com.employee.identity.exception.RoleNotFoundException;
import com.employee.identity.repository.RoleRepository;
import com.employee.identity.repository.UserRepository;
import com.employee.identity.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateUserException(normalizedEmail);
        }

        Role defaultRole = roleRepository
                .findByCodeIgnoreCase(DEFAULT_ROLE)
                .orElseThrow(
                        () -> new RoleNotFoundException(DEFAULT_ROLE)
                );

        User user = new User();

        user.setEmail(normalizedEmail);
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setRoles(
                new HashSet<>(Set.of(defaultRole))
        );

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    private String normalizeEmail(String email) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private UserResponse toResponse(User user) {

        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getEnabled(),
                user.getAccountLocked(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}