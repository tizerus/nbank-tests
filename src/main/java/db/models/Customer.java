package db.models;

import api.models.UserRole;

public record Customer(
        Long id,
        String username,
        String name,
        UserRole role
) {}
