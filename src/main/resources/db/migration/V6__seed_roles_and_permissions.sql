-- =====================================================
-- Roles
-- =====================================================

INSERT INTO roles (code, name, description)
VALUES
    ('ADMIN', 'Administrator', 'Full administrative access'),
    ('USER', 'User', 'Standard application user');


-- =====================================================
-- Permissions
-- =====================================================

INSERT INTO permissions (code, name, description)
VALUES
    (
        'EMPLOYEE_READ',
        'Read Employees',
        'View employee information'
    ),
    (
        'EMPLOYEE_CREATE',
        'Create Employees',
        'Create new employees'
    ),
    (
        'EMPLOYEE_UPDATE',
        'Update Employees',
        'Update existing employees'
    ),
    (
        'EMPLOYEE_DELETE',
        'Delete Employees',
        'Delete employees'
    ),
    (
        'ORGANIZATION_READ',
        'Read Organization',
        'View organization master data'
    ),
    (
        'ORGANIZATION_MANAGE',
        'Manage Organization',
        'Create, update and manage organization master data'
    );


-- =====================================================
-- ADMIN permissions
-- =====================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.code = 'ADMIN';


-- =====================================================
-- USER permissions
-- =====================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.code IN (
        'EMPLOYEE_READ',
        'ORGANIZATION_READ'
    )
WHERE r.code = 'USER';