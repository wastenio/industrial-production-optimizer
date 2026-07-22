import { CurrencyPipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Product } from '../../models';
import { apiErrorMessage } from '../../services/api-error';
import { ProductService } from '../../services/product.service';

@Component({ selector: 'app-product-list', imports: [CurrencyPipe, RouterLink], templateUrl: './product-list.component.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class ProductListComponent implements OnInit {
  private readonly service = inject(ProductService);
  private readonly cdr = inject(ChangeDetectorRef);
  products: Product[] = []; loading = false; errorMessage = '';
  ngOnInit(): void { this.load(); }
  load(): void { this.loading = true; this.errorMessage = ''; this.service.findAll().subscribe({ next: (data) => { this.products = data; }, error: (error) => { this.errorMessage = apiErrorMessage(error, 'Não foi possível carregar os produtos.'); this.loading = false; this.cdr.markForCheck(); }, complete: () => { this.loading = false; this.cdr.markForCheck(); } }); }
  remove(item: Product): void { if (!confirm(`Deseja realmente excluir o produto ${item.code} - ${item.name}?`)) return; this.service.delete(item.id).subscribe({ next: () => this.load(), error: (error) => { this.errorMessage = apiErrorMessage(error, 'Não foi possível excluir o produto.'); this.cdr.markForCheck(); } }); }
}
