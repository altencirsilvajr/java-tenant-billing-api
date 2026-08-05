package com.altencir.tenantbilling.infrastructure;

import com.altencir.tenantbilling.application.BillingStore;
import com.altencir.tenantbilling.domain.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaBillingStore implements BillingStore {
    private final TenantJpaRepository tenants;
    private final PlanJpaRepository plans;
    private final UserJpaRepository users;
    private final SubscriptionJpaRepository subscriptions;
    private final AuditJpaRepository audits;

    public JpaBillingStore(TenantJpaRepository tenants, PlanJpaRepository plans, UserJpaRepository users,
                           SubscriptionJpaRepository subscriptions, AuditJpaRepository audits) {
        this.tenants = tenants; this.plans = plans; this.users = users; this.subscriptions = subscriptions; this.audits = audits;
    }

    @Override public Tenant save(Tenant value) { return tenants.save(new TenantEntity(value)).toDomain(); }
    @Override public BillingPlan save(BillingPlan value) { return plans.save(new PlanEntity(value)).toDomain(); }
    @Override public TenantUser save(TenantUser value) { return users.save(new UserEntity(value)).toDomain(); }
    @Override public Subscription save(Subscription value) { return subscriptions.save(new SubscriptionEntity(value)).toDomain(); }
    @Override public AuditRecord append(AuditRecord value) { return audits.save(new AuditEntity(value)).toDomain(); }
    @Override public Optional<BillingPlan> findPlan(UUID id) { return plans.findById(id).map(PlanEntity::toDomain); }
    @Override public Optional<Subscription> findCurrentSubscription(UUID tenantId) {
        return subscriptions.findFirstByTenantIdAndStatusOrderByStartedAtDesc(tenantId, "ACTIVE").map(SubscriptionEntity::toDomain);
    }
    @Override public List<AuditRecord> findAuditRecords(UUID tenantId) {
        return audits.findAllByTenantIdOrderByOccurredAt(tenantId).stream().map(AuditEntity::toDomain).toList();
    }
}

interface TenantJpaRepository extends JpaRepository<TenantEntity, UUID> { }
interface PlanJpaRepository extends JpaRepository<PlanEntity, UUID> { }
interface UserJpaRepository extends JpaRepository<UserEntity, UUID> { }
interface SubscriptionJpaRepository extends JpaRepository<SubscriptionEntity, UUID> {
    Optional<SubscriptionEntity> findFirstByTenantIdAndStatusOrderByStartedAtDesc(UUID tenantId, String status);
}
interface AuditJpaRepository extends JpaRepository<AuditEntity, UUID> {
    List<AuditEntity> findAllByTenantIdOrderByOccurredAt(UUID tenantId);
}

@Entity @Table(name = "tenants")
class TenantEntity {
    @Id UUID id; @Column(nullable = false) String name; @Column(nullable = false, unique = true) String slug;
    protected TenantEntity() { }
    TenantEntity(Tenant value) { id = value.id(); name = value.name(); slug = value.slug(); }
    Tenant toDomain() { return new Tenant(id, name, slug); }
}

@Entity @Table(name = "billing_plans")
class PlanEntity {
    @Id UUID id; @Column(nullable = false, unique = true) String code; @Column(nullable = false) String name;
    @Column(name = "monthly_amount", nullable = false, precision = 19, scale = 2) BigDecimal monthlyAmount;
    @Column(nullable = false, length = 3) String currency;
    protected PlanEntity() { }
    PlanEntity(BillingPlan value) { id = value.id(); code = value.code(); name = value.name(); monthlyAmount = value.monthlyAmount(); currency = value.currency(); }
    BillingPlan toDomain() { return new BillingPlan(id, code, name, monthlyAmount, currency); }
}

@Entity @Table(name = "tenant_users")
class UserEntity {
    @Id UUID id; @Column(name = "tenant_id", nullable = false) UUID tenantId; @Column(nullable = false) String email;
    @Column(name = "display_name", nullable = false) String displayName; @Column(nullable = false) String role;
    protected UserEntity() { }
    UserEntity(TenantUser value) { id = value.id(); tenantId = value.tenantId(); email = value.email(); displayName = value.displayName(); role = value.role().name(); }
    TenantUser toDomain() { return new TenantUser(id, tenantId, email, displayName, TenantUser.Role.valueOf(role)); }
}

@Entity @Table(name = "subscriptions")
class SubscriptionEntity {
    @Id UUID id; @Column(name = "tenant_id", nullable = false) UUID tenantId; @Column(name = "billing_plan_id", nullable = false) UUID billingPlanId;
    @Column(name = "plan_code", nullable = false) String planCode; @Column(name = "plan_name", nullable = false) String planName;
    @Column(name = "monthly_amount", nullable = false, precision = 19, scale = 2) BigDecimal monthlyAmount;
    @Column(nullable = false, length = 3) String currency; @Column(nullable = false) String status;
    @Column(name = "started_at", nullable = false) Instant startedAt; @Column(name = "renews_at", nullable = false) Instant renewsAt;
    protected SubscriptionEntity() { }
    SubscriptionEntity(Subscription value) {
        id = value.id(); tenantId = value.tenantId(); billingPlanId = value.billingPlanId(); planCode = value.planSnapshot().code();
        planName = value.planSnapshot().name(); monthlyAmount = value.planSnapshot().monthlyAmount(); currency = value.planSnapshot().currency();
        status = value.status(); startedAt = value.startedAt(); renewsAt = value.renewsAt();
    }
    Subscription toDomain() { return new Subscription(id, tenantId, billingPlanId, new PlanSnapshot(planCode, planName, monthlyAmount, currency), status, startedAt, renewsAt); }
}

@Entity @Table(name = "audit_records")
class AuditEntity {
    @Id UUID id; @Column(name = "tenant_id", nullable = false) UUID tenantId; @Column(nullable = false) String action;
    @Column(name = "resource_type", nullable = false) String resourceType; @Column(name = "resource_id", nullable = false) UUID resourceId;
    @Column(name = "occurred_at", nullable = false, updatable = false) Instant occurredAt;
    protected AuditEntity() { }
    AuditEntity(AuditRecord value) { id = value.id(); tenantId = value.tenantId(); action = value.action(); resourceType = value.resourceType(); resourceId = value.resourceId(); occurredAt = value.occurredAt(); }
    AuditRecord toDomain() { return new AuditRecord(id, tenantId, action, resourceType, resourceId, occurredAt); }
}
