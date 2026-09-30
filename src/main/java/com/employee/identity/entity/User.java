package com.employee.identity.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",
                        columnNames = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(
            name = "password_hash",
            nullable = false,
            length = 255
    )
    private String passwordHash;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(
            name = "account_locked",
            nullable = false
    )
    private Boolean accountLocked = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(
                    name = "user_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id"
            )
    )
    private Set<Role> roles =
            new HashSet<>();

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime updatedAt;
}