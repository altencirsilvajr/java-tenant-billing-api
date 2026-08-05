import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TenantBillingApi } from './tenant-billing-api';

describe('TenantBillingApi', () => {
  let api: TenantBillingApi;
  let http: HttpTestingController;
  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    api = TestBed.inject(TenantBillingApi);
    http = TestBed.inject(HttpTestingController);
  });
  afterEach(() => http.verify());

  it('creates a tenant through the backend contract', () => {
    api.createTenant({ name: 'Acme', slug: 'acme' }).subscribe(value => expect(value.tenantId).toBe('tenant-1'));
    const request = http.expectOne('http://localhost:8080/api/tenants');
    expect(request.request.method).toBe('POST');
    request.flush({ tenantId: 'tenant-1', name: 'Acme', slug: 'acme' });
  });

  it('sends the authenticated tenant separately from the target route', () => {
    api.currentSubscription('target-tenant', 'authenticated-tenant').subscribe({ error: () => undefined });
    const request = http.expectOne('http://localhost:8080/api/tenants/target-tenant/subscriptions/current');
    expect(request.request.headers.get('X-Tenant-Id')).toBe('authenticated-tenant');
    request.flush({ title: 'Tenant Boundary Violation' }, { status: 403, statusText: 'Forbidden' });
  });
});
