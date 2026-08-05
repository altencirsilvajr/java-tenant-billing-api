package com.altencir.tenantbilling.domain;

import java.util.Objects;
import java.util.UUID;

public record Tenant(UUID id, String name, String slug) {
    public Tenant {
        Objects.requireNonNull(id);
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (slug == null || !slug.matches("[a-z0-9]+(?:-[a-z0-9]+)*")) throw new IllegalArgumentException("slug is invalid");
        name = name.trim();
    }

    public static Tenant create(String name, String slug) { return new Tenant(UUID.randomUUID(), name, slug); }
}
