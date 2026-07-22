import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { apiErrorMessage } from '../../services/api-error';
import { RawMaterialService } from '../../services/raw-material.service';

@Component({ selector: 'app-raw-material-form', imports: [ReactiveFormsModule, RouterLink], templateUrl: './raw-material-form.component.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class RawMaterialFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly service = inject(RawMaterialService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);
  readonly id = Number(this.route.snapshot.paramMap.get('id')) || null;
  readonly isEditMode = this.id !== null;
  loading = false;
  errorMessage = '';
  readonly form = this.fb.nonNullable.group({ code: ['', Validators.required], name: ['', Validators.required], stockQuantity: [0, [Validators.required, Validators.min(0)]], unit: ['', Validators.required] });

  ngOnInit(): void {
    if (!this.id) return;
    this.loading = true;
    this.service.findById(this.id).pipe(finalize(() => { this.loading = false; this.cdr.markForCheck(); })).subscribe({ next: (item) => this.form.patchValue(item), error: (error) => { this.errorMessage = apiErrorMessage(error, 'Não foi possível carregar a matéria-prima.'); } });
  }
  save(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true; this.errorMessage = '';
    const request = this.id ? this.service.update(this.id, this.form.getRawValue()) : this.service.create(this.form.getRawValue());
    request.pipe(finalize(() => { this.loading = false; this.cdr.markForCheck(); })).subscribe({ next: () => void this.router.navigate(['/raw-materials']), error: (error) => { this.errorMessage = apiErrorMessage(error, this.isEditMode ? 'Não foi possível atualizar a matéria-prima.' : 'Não foi possível cadastrar a matéria-prima.'); } });
  }
}
