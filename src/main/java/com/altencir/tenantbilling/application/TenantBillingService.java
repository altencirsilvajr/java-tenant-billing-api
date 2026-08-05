package com.altencir.tenantbilling.application;

import com.altencir.tenantbilling.domain.*;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantBillingService {
    private final BillingStore store;
    private final Clock clock;

    public TenantBillingService(BillingStore store, Clock clock) { this.store = store; this.clock = clock; }

    @Transactional
    public Tenant createTenant(String name, String slug) {
        var tenant = store.save(Tenant.create(name, slug));
        store.append(AuditRecord.create(tenant.id(), "TENANT_CREATED", "TENANT", tenant.id(), clock.instant()));
        return tenant;
    }

    @Transactional
    public BillingPlan createPlan(String code, String name, BigDecimal amount, String currency) {
        return store.save(BillingPlan.create(code, name, amount, currency));
    }

    @Transactional
    public TenantUser createUser(UUID authenticatedTenantId, UUID targetTenantId, String email, String displayName, TenantUser.Role role) {
        TenantBoundary.requireAccess(authenticatedTenantId, targetTenantId);
        var user = store.save(TenantUser.create(targetTenantId, email, displayName, role));
        store.append(AuditRecord.create(targetTenantId, "TENANT_USER_CREATED", "TENANT_USER", user.id(), clock.instant()));
        return user;
    }

    @Transactional
    public Subscription subscribe(UUID authenticatedTenantId, UUID targetTenantId, UUID planId) {
        TenantBoundary.requireAccess(authenticatedTenantId, targetTenantId);
        var plan = store.findPlan(planId).orElseThrow(() -> new ResourceNotFound("Billing plan not found"));
        var subscription = store.save(Subscription.start(targetTenantId, plan, clock.instant()));
        store.append(AuditRecord.create(targetTenantId, "SUBSCRIPTION_STARTED", "SUBSCRIPTION", subscription.id(), clock.instant()));
        return subscription;
    }

    @Transactional(readOnly = true)
    public Subscription currentSubscription(UUID authenticatedTenantId, UUID targetTenantId) {
        TenantBoundary.requireAccess(authenticatedTenantId, targetTenantId);
        return store.findCurrentSubscription(targetTenantId).orElseThrow(() -> new ResourceNotFound("Current subscription not found"));
    }

    @Transactional(readOnly = true)
    public List<AuditRecord> auditRecords(UUID authenticatedTenantId, UUID targetTenantId) {
        TenantBoundary.requireAccess(authenticatedTenantId, targetTenantId);
        return store.findAuditRecords(targetTenantId);
    }
}
