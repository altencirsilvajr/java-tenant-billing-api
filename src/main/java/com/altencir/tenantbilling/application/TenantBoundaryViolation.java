package com.altencir.tenantbilling.application;

import java.util.UUID;

public final class TenantBoundaryViolation extends RuntimeException {
    public TenantBoundaryViolation(UUID authenticatedTenantId, UUID targetTenantId) {
        super("Tenant %s cannot access tenant %s".formatted(authenticatedTenantId, targetTenantId));
    }
}
