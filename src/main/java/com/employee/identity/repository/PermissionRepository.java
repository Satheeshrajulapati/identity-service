package com.employee.identity.repository;

import com.employee.identity.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository
        extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);
}