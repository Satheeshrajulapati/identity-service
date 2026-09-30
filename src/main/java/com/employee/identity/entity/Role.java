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
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_roles_code",
                        columnNames = "code"
                ),
                @UniqueConstraint(
                        name = "uk_roles_name",
                        columnNames = "name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(
                    name = "role_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "permission_id"
            )
    )
    private Set<Permission> permissions =
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