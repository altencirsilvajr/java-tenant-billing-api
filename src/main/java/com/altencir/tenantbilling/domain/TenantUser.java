package com.altencir.tenantbilling.domain;

import java.util.UUID;

public record TenantUser(UUID id, UUID tenantId, String email, String displayName, Role role) {
    public enum Role { OWNER, ADMIN, MEMBER }

    public static TenantUser create(UUID tenantId, String email, String displayName, Role role) {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("email is invalid");
        if (displayName == null || displayName.isBlank()) throw new IllegalArgumentException("displayName is required");
        return new TenantUser(UUID.randomUUID(), tenantId, email.toLowerCase(), displayName.trim(), role);
    }
}
