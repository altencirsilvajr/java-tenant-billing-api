package com.altencir.tenantbilling.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

public record BillingPlan(UUID id, String code, String name, BigDecimal monthlyAmount, String currency) {
    public BillingPlan {
        Objects.requireNonNull(id, "id is required");
        code = required(code, "code").toLowerCase();
        name = required(name, "name");
        Objects.requireNonNull(monthlyAmount, "monthlyAmount is required");
        if (monthlyAmount.signum() <= 0) throw new IllegalArgumentException("Monthly amount must be positive");
        currency = Currency.getInstance(required(currency, "currency").toUpperCase()).getCurrencyCode();
        monthlyAmount = monthlyAmount.setScale(2);
    }

    public static BillingPlan create(String code, String name, BigDecimal monthlyAmount, String currency) {
        return new BillingPlan(UUID.randomUUID(), code, name, monthlyAmount, currency);
    }

    private static String required(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        return value.trim();
    }
}
