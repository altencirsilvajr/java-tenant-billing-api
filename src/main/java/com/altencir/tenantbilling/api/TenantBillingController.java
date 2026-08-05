package com.altencir.tenantbilling.api;

import com.altencir.tenantbilling.application.TenantBillingService;
import com.altencir.tenantbilling.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Tenant Billing")
public class TenantBillingController {
    private final TenantBillingService service;
    public TenantBillingController(TenantBillingService service) { this.service = service; }

    @PostMapping("/tenants") @Operation(summary = "Create a tenant")
    ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        var tenant = service.createTenant(request.name(), request.slug());
        return ResponseEntity.created(URI.create("/api/tenants/" + tenant.id())).body(TenantResponse.from(tenant));
    }

    @PostMapping("/plans") @Operation(summary = "Create a global billing plan")
    ResponseEntity<PlanResponse> createPlan(@Valid @RequestBody CreatePlanRequest request) {
        var plan = service.createPlan(request.code(), request.name(), request.monthlyAmount(), request.currency());
        return ResponseEntity.created(URI.create("/api/plans/" + plan.id())).body(PlanResponse.from(plan));
    }

    @PostMapping("/tenants/{tenantId}/users") @Operation(summary = "Create a user inside the authenticated tenant")
    ResponseEntity<UserResponse> createUser(@RequestHeader("X-Tenant-Id") UUID authenticatedTenantId, @PathVariable UUID tenantId,
                                            @Valid @RequestBody CreateUserRequest request) {
        var user = service.createUser(authenticatedTenantId, tenantId, request.email(), request.displayName(), request.role());
        return ResponseEntity.created(URI.create("/api/tenants/%s/users/%s".formatted(tenantId, user.id()))).body(UserResponse.from(user));
    }

    @PostMapping("/tenants/{tenantId}/subscriptions") @Operation(summary = "Subscribe the authenticated tenant")
    ResponseEntity<SubscriptionResponse> subscribe(@RequestHeader("X-Tenant-Id") UUID authenticatedTenantId, @PathVariable UUID tenantId,
                                                   @Valid @RequestBody SubscribeRequest request) {
        var subscription = service.subscribe(authenticatedTenantId, tenantId, request.billingPlanId());
        return ResponseEntity.created(URI.create("/api/tenants/%s/subscriptions/current".formatted(tenantId)))
                .body(SubscriptionResponse.from(subscription));
    }

    @GetMapping("/tenants/{tenantId}/subscriptions/current") @Operation(summary = "Read the authenticated tenant current subscription")
    SubscriptionResponse current(@RequestHeader("X-Tenant-Id") UUID authenticatedTenantId, @PathVariable UUID tenantId) {
        return SubscriptionResponse.from(service.currentSubscription(authenticatedTenantId, tenantId));
    }

    @GetMapping("/tenants/{tenantId}/audit-records") @Operation(summary = "Read append-only audit records for the authenticated tenant")
    List<AuditResponse> audit(@RequestHeader("X-Tenant-Id") UUID authenticatedTenantId, @PathVariable UUID tenantId) {
        return service.auditRecords(authenticatedTenantId, tenantId).stream().map(AuditResponse::from).toList();
    }

    public record CreateTenantRequest(@NotBlank @Size(max = 160) String name,
                                      @NotBlank @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*") String slug) { }
    public record CreatePlanRequest(@NotBlank String code, @NotBlank String name,
                                    @NotNull @DecimalMin(value = "0.01") BigDecimal monthlyAmount,
                                    @NotBlank @Size(min = 3, max = 3) String currency) { }
    public record CreateUserRequest(@NotBlank @Email String email, @NotBlank String displayName, @NotNull TenantUser.Role role) { }
    public record SubscribeRequest(@NotNull UUID billingPlanId) { }
    public record TenantResponse(UUID tenantId, String name, String slug) {
        static TenantResponse from(Tenant value) { return new TenantResponse(value.id(), value.name(), value.slug()); }
    }
    public record PlanResponse(UUID billingPlanId, String code, String name, BigDecimal monthlyAmount, String currency) {
        static PlanResponse from(BillingPlan value) { return new PlanResponse(value.id(), value.code(), value.name(), value.monthlyAmount(), value.currency()); }
    }
    public record UserResponse(UUID userId, UUID tenantId, String email, String displayName, TenantUser.Role role) {
        static UserResponse from(TenantUser value) { return new UserResponse(value.id(), value.tenantId(), value.email(), value.displayName(), value.role()); }
    }
    public record SubscriptionResponse(UUID subscriptionId, UUID tenantId, UUID billingPlanId, String planCode, String planName,
                                       BigDecimal monthlyAmount, String currency, String status, Instant startedAt, Instant renewsAt) {
        static SubscriptionResponse from(Subscription value) {
            return new SubscriptionResponse(value.id(), value.tenantId(), value.billingPlanId(), value.planSnapshot().code(),
                    value.planSnapshot().name(), value.planSnapshot().monthlyAmount(), value.planSnapshot().currency(), value.status(), value.startedAt(), value.renewsAt());
        }
    }
    public record AuditResponse(UUID auditRecordId, UUID tenantId, String action, String resourceType, UUID resourceId, Instant occurredAt) {
        static AuditResponse from(AuditRecord value) { return new AuditResponse(value.id(), value.tenantId(), value.action(), value.resourceType(), value.resourceId(), value.occurredAt()); }
    }
}
