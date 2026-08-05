import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

export interface Tenant { tenantId: string; name: string; slug: string; }
export interface Plan { billingPlanId: string; code: string; name: string; monthlyAmount: number; currency: string; }
export interface Subscription { subscriptionId: string; tenantId: string; planCode: string; planName: string; monthlyAmount: number; currency: string; status: string; renewsAt: string; }

@Injectable({ providedIn: 'root' })
export class TenantBillingApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api';

  createTenant(body: { name: string; slug: string }) { return this.http.post<Tenant>(`${this.baseUrl}/tenants`, body); }
  createPlan(body: { code: string; name: string; monthlyAmount: number; currency: string }) { return this.http.post<Plan>(`${this.baseUrl}/plans`, body); }
  createUser(tenantId: string, body: { email: string; displayName: string; role: string }) {
    return this.http.post(`${this.baseUrl}/tenants/${tenantId}/users`, body, { headers: this.tenantHeaders(tenantId) });
  }
  subscribe(tenantId: string, billingPlanId: string) {
    return this.http.post<Subscription>(`${this.baseUrl}/tenants/${tenantId}/subscriptions`, { billingPlanId }, { headers: this.tenantHeaders(tenantId) });
  }
  currentSubscription(targetTenantId: string, authenticatedTenantId: string) {
    return this.http.get<Subscription>(`${this.baseUrl}/tenants/${targetTenantId}/subscriptions/current`, { headers: this.tenantHeaders(authenticatedTenantId) });
  }
  auditRecords(tenantId: string) {
    return this.http.get<unknown[]>(`${this.baseUrl}/tenants/${tenantId}/audit-records`, { headers: this.tenantHeaders(tenantId) });
  }
  private tenantHeaders(tenantId: string) { return new HttpHeaders({ 'X-Tenant-Id': tenantId }); }
}
