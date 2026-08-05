create table tenants (
    id uuid primary key,
    name varchar(160) not null,
    slug varchar(80) not null unique
);

create table billing_plans (
    id uuid primary key,
    code varchar(80) not null unique,
    name varchar(160) not null,
    monthly_amount numeric(19,2) not null check (monthly_amount > 0),
    currency varchar(3) not null
);

create table tenant_users (
    id uuid primary key,
    tenant_id uuid not null references tenants(id),
    email varchar(254) not null,
    display_name varchar(160) not null,
    role varchar(20) not null,
    unique (tenant_id, email)
);
create index ix_tenant_users_tenant on tenant_users(tenant_id, id);

create table subscriptions (
    id uuid primary key,
    tenant_id uuid not null references tenants(id),
    billing_plan_id uuid not null references billing_plans(id),
    plan_code varchar(80) not null,
    plan_name varchar(160) not null,
    monthly_amount numeric(19,2) not null,
    currency varchar(3) not null,
    status varchar(20) not null,
    started_at timestamptz not null,
    renews_at timestamptz not null
);
create unique index ux_subscriptions_active_tenant on subscriptions(tenant_id) where status = 'ACTIVE';
create index ix_subscriptions_tenant_status on subscriptions(tenant_id, status, started_at desc);

create table audit_records (
    id uuid primary key,
    tenant_id uuid not null references tenants(id),
    action varchar(80) not null,
    resource_type varchar(80) not null,
    resource_id uuid not null,
    occurred_at timestamptz not null
);
create index ix_audit_records_tenant_time on audit_records(tenant_id, occurred_at);

create function reject_audit_mutation() returns trigger language plpgsql as $$
begin
    raise exception 'audit_records are append-only';
end;
$$;
create trigger audit_records_append_only before update or delete on audit_records
for each row execute function reject_audit_mutation();
