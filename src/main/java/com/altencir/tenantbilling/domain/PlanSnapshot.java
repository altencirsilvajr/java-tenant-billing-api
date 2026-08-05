package com.altencir.tenantbilling.domain;

import java.math.BigDecimal;

public record PlanSnapshot(String code, String name, BigDecimal monthlyAmount, String currency) {
    public static PlanSnapshot from(BillingPlan plan) {
        return new PlanSnapshot(plan.code(), plan.name(), plan.monthlyAmount(), plan.currency());
    }
}
