package com.altencir.tenantbilling.application;

import java.util.Objects;
import java.util.UUID;

public final class TenantBoundary {
    private TenantBoundary() { }

    public static void requireAccess(UUID authenticatedTenantId, UUID targetTenantId) {
        if (!Objects.equals(authenticatedTenantId, targetTenantId)) {
            throw new TenantBoundaryViolation(authenticatedTenantId, targetTenantId);
        }
    }
}
