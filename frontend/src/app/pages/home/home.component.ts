import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ProductService } from '../../services/product.service';
import { RawMaterialService } from '../../services/raw-material.service';

@Component({ selector: 'app-home', imports: [RouterLink], templateUrl: './home.component.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class HomeComponent implements OnInit {
  private readonly rawMaterialService = inject(RawMaterialService);
  private readonly productService = inject(ProductService);
  private readonly cdr = inject(ChangeDetectorRef);
  loading = false;
  errorMessage = '';
  rawMaterialsCount = 0;
  productsCount = 0;

  ngOnInit(): void {
    this.loading = true;
    forkJoin({ rawMaterials: this.rawMaterialService.findAll(), products: this.productService.findAll() }).subscribe({
      next: ({ rawMaterials, products }) => { this.rawMaterialsCount = rawMaterials.length; this.productsCount = products.length; },
      error: () => { this.errorMessage = 'Não foi possível carregar os dados do dashboard.'; this.loading = false; this.cdr.markForCheck(); },
      complete: () => { this.loading = false; this.cdr.markForCheck(); },
    });
  }
}
