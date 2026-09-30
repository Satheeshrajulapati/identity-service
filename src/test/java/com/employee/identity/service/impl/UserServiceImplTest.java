package com.employee.identity.service.impl;

import com.employee.identity.dto.request.CreateUserRequest;
import com.employee.identity.dto.response.UserResponse;
import com.employee.identity.entity.Role;
import com.employee.identity.entity.User;
import com.employee.identity.exception.DuplicateUserException;
import com.employee.identity.exception.RoleNotFoundException;
import com.employee.identity.repository.RoleRepository;
import com.employee.identity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(
                userRepository,
                roleRepository,
                passwordEncoder
        );
    }

    @Test
    void shouldCreateUserWithDefaultUserRole() {

        CreateUserRequest request =
                new CreateUserRequest(
                        " Employee@Company.com ",
                        "Employee@123"
                );

        Role userRole = new Role();
        userRole.setId(2L);
        userRole.setCode("USER");
        userRole.setName("User");
        userRole.setActive(true);

        when(userRepository.existsByEmailIgnoreCase(
                "employee@company.com"
        )).thenReturn(false);

        when(roleRepository.findByCodeIgnoreCase("USER"))
                .thenReturn(Optional.of(userRole));

        when(passwordEncoder.encode("Employee@123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        UserResponse response =
                userService.createUser(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(
                "employee@company.com",
                response.email()
        );
        assertTrue(response.enabled());
        assertFalse(response.accountLocked());
        assertTrue(response.roles().contains("USER"));

        verify(passwordEncoder)
                .encode("Employee@123");

        verify(roleRepository)
                .findByCodeIgnoreCase("USER");

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void shouldStoreEncodedPasswordInsteadOfRawPassword() {

        CreateUserRequest request =
                new CreateUserRequest(
                        "employee@company.com",
                        "Employee@123"
                );

        Role userRole = new Role();
        userRole.setId(2L);
        userRole.setCode("USER");

        when(userRepository.existsByEmailIgnoreCase(
                "employee@company.com"
        )).thenReturn(false);

        when(roleRepository.findByCodeIgnoreCase("USER"))
                .thenReturn(Optional.of(userRole));

        when(passwordEncoder.encode("Employee@123"))
                .thenReturn("$2a$encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        userService.createUser(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
                "$2a$encoded-password",
                savedUser.getPasswordHash()
        );

        assertNotEquals(
                "Employee@123",
                savedUser.getPasswordHash()
        );
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        CreateUserRequest request =
                new CreateUserRequest(
                        " Employee@Company.com ",
                        "Employee@123"
                );

        when(userRepository.existsByEmailIgnoreCase(
                "employee@company.com"
        )).thenReturn(true);

        DuplicateUserException exception =
                assertThrows(
                        DuplicateUserException.class,
                        () -> userService.createUser(request)
                );

        assertEquals(
                "User already exists with email: employee@company.com",
                exception.getMessage()
        );

        verify(roleRepository, never())
                .findByCodeIgnoreCase(anyString());

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenDefaultRoleDoesNotExist() {

        CreateUserRequest request =
                new CreateUserRequest(
                        "employee@company.com",
                        "Employee@123"
                );

        when(userRepository.existsByEmailIgnoreCase(
                "employee@company.com"
        )).thenReturn(false);

        when(roleRepository.findByCodeIgnoreCase("USER"))
                .thenReturn(Optional.empty());

        RoleNotFoundException exception =
                assertThrows(
                        RoleNotFoundException.class,
                        () -> userService.createUser(request)
                );

        assertEquals(
                "Role not found with code: USER",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }
}