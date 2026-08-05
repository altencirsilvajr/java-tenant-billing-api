package com.altencir.tenantbilling.domain;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

public record Subscription(UUID id, UUID tenantId, UUID billingPlanId, PlanSnapshot planSnapshot,
                           String status, Instant startedAt, Instant renewsAt) {
    public Subscription {
        Objects.requireNonNull(id);
        Objects.requireNonNull(tenantId);
        Objects.requireNonNull(billingPlanId);
        Objects.requireNonNull(planSnapshot);
        Objects.requireNonNull(startedAt);
        Objects.requireNonNull(renewsAt);
    }

    public static Subscription start(UUID tenantId, BillingPlan plan, Instant now) {
        var renewal = now.atZone(ZoneOffset.UTC).plusMonths(1).toInstant();
        return new Subscription(UUID.randomUUID(), tenantId, plan.id(), PlanSnapshot.from(plan), "ACTIVE", now, renewal);
    }
}
