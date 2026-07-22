import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { ProductFormComponent } from './pages/products/product-form.component';
import { ProductListComponent } from './pages/products/product-list.component';
import { ProductionPlanComponent } from './pages/production-plan/production-plan.component';
import { RawMaterialFormComponent } from './pages/raw-materials/raw-material-form.component';
import { RawMaterialListComponent } from './pages/raw-materials/raw-material-list.component';

export const routes: Routes = [
  { path: '', component: HomeComponent, title: 'Dashboard' },
  { path: 'raw-materials', component: RawMaterialListComponent, title: 'Matérias-primas' },
  { path: 'raw-materials/new', component: RawMaterialFormComponent, title: 'Nova matéria-prima' },
  { path: 'raw-materials/:id/edit', component: RawMaterialFormComponent, title: 'Editar matéria-prima' },
  { path: 'products', component: ProductListComponent, title: 'Produtos' },
  { path: 'products/new', component: ProductFormComponent, title: 'Novo produto' },
  { path: 'products/:id/edit', component: ProductFormComponent, title: 'Editar produto' },
  { path: 'production-plan', component: ProductionPlanComponent, title: 'Plano de produção' },
  { path: '**', redirectTo: '' },
];
