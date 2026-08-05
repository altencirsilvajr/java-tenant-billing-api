package com.altencir.tenantbilling.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TenantBillingFlowIT {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort int port;
    @Autowired TestRestTemplate http;

    @Test
    void tenant_can_complete_billing_flow_and_read_its_audit_trail() {
        var suffix = UUID.randomUUID().toString().substring(0, 8);
        var tenant = post("/api/tenants", Map.of("name", "Acme " + suffix, "slug", "acme-" + suffix), null);
        var plan = post("/api/plans", Map.of("code", "starter-" + suffix, "name", "Starter", "monthlyAmount", new BigDecimal("49.90"), "currency", "BRL"), null);
        var tenantId = tenant.getBody().get("tenantId").toString();
        var planId = plan.getBody().get("billingPlanId").toString();

        var user = post("/api/tenants/" + tenantId + "/users", Map.of("email", "owner@acme.io", "displayName", "Acme Owner", "role", "OWNER"), tenantId);
        var subscription = post("/api/tenants/" + tenantId + "/subscriptions", Map.of("billingPlanId", planId), tenantId);
        var current = get("/api/tenants/" + tenantId + "/subscriptions/current", tenantId, Map.class);
        var audit = get("/api/tenants/" + tenantId + "/audit-records", tenantId, Object[].class);

        assertThat(user.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(subscription.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(current.getBody().get("planCode")).isEqualTo("starter-" + suffix);
        assertThat(current.getBody().get("monthlyAmount").toString()).isEqualTo("49.9");
        assertThat(audit.getBody()).hasSize(3);
    }

    @Test
    void route_rejects_authenticated_tenant_from_another_tenant_before_reading() {
        var suffix = UUID.randomUUID().toString().substring(0, 8);
        var target = post("/api/tenants", Map.of("name", "Target", "slug", "target-" + suffix), null);
        var attacker = post("/api/tenants", Map.of("name", "Attacker", "slug", "attacker-" + suffix), null);
        var targetId = target.getBody().get("tenantId").toString();
        var attackerId = attacker.getBody().get("tenantId").toString();

        var response = get("/api/tenants/" + targetId + "/subscriptions/current", attackerId, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().get("title")).isEqualTo("Tenant Boundary Violation");
    }

    @SuppressWarnings("unchecked")
    private org.springframework.http.ResponseEntity<Map<String, Object>> post(String path, Map<String, ?> body, String tenantId) {
        var headers = headers(tenantId);
        return http.exchange(url(path), HttpMethod.POST, new HttpEntity<>(body, headers), (Class<Map<String, Object>>) (Class<?>) Map.class);
    }

    private <T> org.springframework.http.ResponseEntity<T> get(String path, String tenantId, Class<T> type) {
        return http.exchange(url(path), HttpMethod.GET, new HttpEntity<>(headers(tenantId)), type);
    }

    private HttpHeaders headers(String tenantId) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (tenantId != null) headers.set("X-Tenant-Id", tenantId);
        return headers;
    }

    private String url(String path) { return "http://localhost:" + port + path; }
}
