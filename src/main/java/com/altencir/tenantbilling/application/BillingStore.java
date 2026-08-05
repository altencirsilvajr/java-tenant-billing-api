package com.altencir.tenantbilling.application;

import com.altencir.tenantbilling.domain.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BillingStore {
    Tenant save(Tenant tenant);
    BillingPlan save(BillingPlan plan);
    TenantUser save(TenantUser user);
    Subscription save(Subscription subscription);
    AuditRecord append(AuditRecord auditRecord);
    Optional<BillingPlan> findPlan(UUID planId);
    Optional<Subscription> findCurrentSubscription(UUID tenantId);
    List<AuditRecord> findAuditRecords(UUID tenantId);
}
