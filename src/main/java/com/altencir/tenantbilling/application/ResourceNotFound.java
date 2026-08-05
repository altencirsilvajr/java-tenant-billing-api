package com.altencir.tenantbilling.application;

public final class ResourceNotFound extends RuntimeException {
    public ResourceNotFound(String message) { super(message); }
}
