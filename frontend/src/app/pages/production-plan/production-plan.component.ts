import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject } from '@angular/core';
import { ProductionPlan } from '../../models';
import { apiErrorMessage } from '../../services/api-error';
import { ProductionPlanService } from '../../services/production-plan.service';

@Component({ selector: 'app-production-plan', imports: [CurrencyPipe, DecimalPipe], templateUrl: './production-plan.component.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class ProductionPlanComponent {
  private readonly service = inject(ProductionPlanService); private readonly cdr = inject(ChangeDetectorRef);
  loading = false; errorMessage = ''; plan: ProductionPlan | null = null;
  calculate(): void { this.loading = true; this.errorMessage = ''; this.plan = null; this.service.calculate().subscribe({ next: (plan) => { this.plan = plan; }, error: (error) => { this.errorMessage = apiErrorMessage(error, 'Não foi possível calcular o plano de produção.'); this.loading = false; this.cdr.markForCheck(); }, complete: () => { this.loading = false; this.cdr.markForCheck(); } }); }
}
