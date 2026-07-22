import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ProductionPlan } from '../models';

@Injectable({ providedIn: 'root' })
export class ProductionPlanService {
  private readonly http = inject(HttpClient);

  calculate(): Observable<ProductionPlan> {
    return this.http.post<ProductionPlan>('http://localhost:8080/api/production-plans/calculate', null);
  }
}
