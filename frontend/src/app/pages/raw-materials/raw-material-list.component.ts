import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { RawMaterial } from '../../models';
import { apiErrorMessage } from '../../services/api-error';
import { RawMaterialService } from '../../services/raw-material.service';

@Component({ selector: 'app-raw-material-list', imports: [RouterLink], templateUrl: './raw-material-list.component.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class RawMaterialListComponent implements OnInit {
  private readonly service = inject(RawMaterialService);
  private readonly cdr = inject(ChangeDetectorRef);
  rawMaterials: RawMaterial[] = [];
  loading = false;
  errorMessage = '';

  ngOnInit(): void { this.load(); }
  load(): void {
    this.loading = true; this.errorMessage = '';
    this.service.findAll().subscribe({
      next: (data) => { this.rawMaterials = data; },
      error: (error) => { this.errorMessage = apiErrorMessage(error, 'Não foi possível carregar as matérias-primas.'); this.loading = false; this.cdr.markForCheck(); },
      complete: () => { this.loading = false; this.cdr.markForCheck(); },
    });
  }
  remove(item: RawMaterial): void {
    if (!confirm(`Deseja realmente excluir a matéria-prima ${item.code} - ${item.name}?`)) return;
    this.service.delete(item.id).subscribe({ next: () => this.load(), error: (error) => { this.errorMessage = apiErrorMessage(error, 'Não foi possível excluir a matéria-prima.'); this.cdr.markForCheck(); } });
  }
}
