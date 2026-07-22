import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin, of, finalize } from 'rxjs';
import { Product, ProductPayload, RawMaterial } from '../../models';
import { apiErrorMessage } from '../../services/api-error';
import { ProductService } from '../../services/product.service';
import { RawMaterialService } from '../../services/raw-material.service';

@Component({ selector: 'app-product-form', imports: [ReactiveFormsModule, RouterLink], templateUrl: './product-form.component.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class ProductFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder); private readonly products = inject(ProductService); private readonly rawMaterialsService = inject(RawMaterialService); private readonly route = inject(ActivatedRoute); private readonly router = inject(Router); private readonly cdr = inject(ChangeDetectorRef);
  readonly id = Number(this.route.snapshot.paramMap.get('id')) || null; readonly isEditMode = this.id !== null;
  rawMaterials: RawMaterial[] = []; loading = false; errorMessage = '';
  readonly form = this.fb.nonNullable.group({ code: ['', Validators.required], name: ['', Validators.required], salePrice: [0, [Validators.required, Validators.min(0.01)]], compositions: this.fb.array([]) });
  get compositions(): FormArray { return this.form.controls.compositions; }

  ngOnInit(): void {
    this.loading = true;
    forkJoin({ rawMaterials: this.rawMaterialsService.findAll(), product: this.id ? this.products.findById(this.id) : of(null) }).pipe(finalize(() => { this.loading = false; this.cdr.markForCheck(); })).subscribe({ next: ({ rawMaterials, product }) => { this.rawMaterials = rawMaterials; if (product) this.fill(product); else this.addComposition(); }, error: (error) => { this.errorMessage = apiErrorMessage(error, 'Não foi possível carregar os dados do formulário.'); } });
  }
  addComposition(rawMaterialId: number | null = null, requiredQuantity = 0): void { this.compositions.push(this.fb.group({ rawMaterialId: [rawMaterialId, Validators.required], requiredQuantity: [requiredQuantity, [Validators.required, Validators.min(0.001)]] })); }
  removeComposition(index: number): void { this.compositions.removeAt(index); }
  private fill(product: Product): void { this.form.patchValue({ code: product.code, name: product.name, salePrice: product.salePrice }); product.compositions.forEach((item) => this.addComposition(item.rawMaterialId, item.requiredQuantity)); }
  save(): void {
    if (this.form.invalid || this.compositions.length === 0) { this.form.markAllAsTouched(); this.errorMessage = this.compositions.length ? 'Revise os campos obrigatórios.' : 'Adicione ao menos uma matéria-prima.'; return; }
    this.loading = true; this.errorMessage = '';
    const value = this.form.getRawValue();
    const compositionValues = value.compositions as Array<{
      rawMaterialId: number | null;
      requiredQuantity: number;
    }>;
    const payload: ProductPayload = {
      code: value.code,
      name: value.name,
      salePrice: value.salePrice,
      compositions: compositionValues.map((item) => ({
        rawMaterialId: Number(item.rawMaterialId),
        requiredQuantity: Number(item.requiredQuantity),
      })),
    };
    const request = this.id ? this.products.update(this.id, payload) : this.products.create(payload);
    request.pipe(finalize(() => { this.loading = false; this.cdr.markForCheck(); })).subscribe({ next: () => void this.router.navigate(['/products']), error: (error) => { this.errorMessage = apiErrorMessage(error, this.isEditMode ? 'Não foi possível atualizar o produto.' : 'Não foi possível cadastrar o produto.'); } });
  }
}
