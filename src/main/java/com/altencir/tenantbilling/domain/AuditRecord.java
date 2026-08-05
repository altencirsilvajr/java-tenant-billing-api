package com.altencir.tenantbilling.domain;

import java.time.Instant;
import java.util.UUID;

public record AuditRecord(UUID id, UUID tenantId, String action, String resourceType, UUID resourceId, Instant occurredAt) {
    public static AuditRecord create(UUID tenantId, String action, String resourceType, UUID resourceId, Instant now) {
        return new AuditRecord(UUID.randomUUID(), tenantId, action, resourceType, resourceId, now);
    }
}
