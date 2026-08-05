import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { catchError, concatMap, forkJoin, of, tap } from 'rxjs';
import { TenantBillingApi } from './tenant-billing-api';

@Component({ selector: 'app-root', imports: [CommonModule], templateUrl: './app.html', styleUrl: './app.css' })
export class App {
  private readonly api = inject(TenantBillingApi);
  readonly running = signal(false);
  readonly result = signal<unknown>(null);
  readonly error = signal<unknown>(null);
  private currentTenantId = '';

  runFlow() {
    const suffix = Date.now().toString(36);
    this.begin();
    forkJoin({
      tenant: this.api.createTenant({ name: `Acme ${suffix}`, slug: `acme-${suffix}` }),
      plan: this.api.createPlan({ code: `starter-${suffix}`, name: 'Starter', monthlyAmount: 49.9, currency: 'BRL' })
    }).pipe(
      tap(({ tenant }) => this.currentTenantId = tenant.tenantId),
      concatMap(({ tenant, plan }) => forkJoin({
        tenant: of(tenant), plan: of(plan),
        user: this.api.createUser(tenant.tenantId, { email: `owner-${suffix}@acme.io`, displayName: 'Acme Owner', role: 'OWNER' })
      })),
      concatMap(({ tenant, plan, user }) => this.api.subscribe(tenant.tenantId, plan.billingPlanId).pipe(
        concatMap(subscription => forkJoin({ subscription: of(subscription), user: of(user), audit: this.api.auditRecords(tenant.tenantId) }))
      )),
      catchError(error => { this.fail(error); return of(null); })
    ).subscribe(value => { if (value) this.result.set(value); this.running.set(false); });
  }

  testCrossTenant() {
    if (!this.currentTenantId) { this.error.set('Execute o fluxo completo primeiro.'); return; }
    const suffix = Date.now().toString(36);
    this.begin();
    this.api.createTenant({ name: `Attacker ${suffix}`, slug: `attacker-${suffix}` }).pipe(
      concatMap(attacker => this.api.currentSubscription(this.currentTenantId, attacker.tenantId)),
      catchError(error => { this.result.set({ expectedStatus: 403, received: error.status, problem: error.error }); return of(null); })
    ).subscribe(() => this.running.set(false));
  }

  private begin() { this.running.set(true); this.result.set(null); this.error.set(null); }
  private fail(error: unknown) { this.error.set(error); this.running.set(false); }
}
