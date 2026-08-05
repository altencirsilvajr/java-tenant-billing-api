package com.altencir.tenantbilling.application;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class TenantBoundaryTest {
    @Test
    void allows_the_requested_tenant() {
        var tenantId = UUID.randomUUID();
        assertThatCode(() -> TenantBoundary.requireAccess(tenantId, tenantId)).doesNotThrowAnyException();
    }

    @Test
    void rejects_cross_tenant_access() {
        assertThatThrownBy(() -> TenantBoundary.requireAccess(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(TenantBoundaryViolation.class);
    }
}
