package com.altencir.tenantbilling.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BillingDomainTest {
    @Test
    void subscription_keeps_plan_snapshot_when_created() {
        var tenantId = UUID.randomUUID();
        var plan = BillingPlan.create("starter", "Starter", new BigDecimal("49.90"), "BRL");

        var subscription = Subscription.start(tenantId, plan, Instant.parse("2026-08-05T12:00:00Z"));

        assertThat(subscription.planSnapshot().code()).isEqualTo("starter");
        assertThat(subscription.planSnapshot().monthlyAmount()).isEqualByComparingTo("49.90");
        assertThat(subscription.renewsAt()).isEqualTo(Instant.parse("2026-09-05T12:00:00Z"));
    }

    @Test
    void plan_rejects_non_positive_monthly_amount() {
        assertThatThrownBy(() -> BillingPlan.create("free", "Free", BigDecimal.ZERO, "BRL"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Monthly amount must be positive");
    }
}
