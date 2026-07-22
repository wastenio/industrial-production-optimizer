import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Product, ProductPayload } from '../models';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = 'http://localhost:8080/api/products';

  findAll(): Observable<Product[]> { return this.http.get<Product[]>(this.endpoint); }
  findById(id: number): Observable<Product> { return this.http.get<Product>(`${this.endpoint}/${id}`); }
  create(payload: ProductPayload): Observable<Product> { return this.http.post<Product>(this.endpoint, payload); }
  update(id: number, payload: ProductPayload): Observable<Product> { return this.http.put<Product>(`${this.endpoint}/${id}`, payload); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.endpoint}/${id}`); }
}
