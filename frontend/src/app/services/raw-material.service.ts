import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RawMaterial, RawMaterialPayload } from '../models';

@Injectable({ providedIn: 'root' })
export class RawMaterialService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = 'http://localhost:8080/api/raw-materials';

  findAll(): Observable<RawMaterial[]> { return this.http.get<RawMaterial[]>(this.endpoint); }
  findById(id: number): Observable<RawMaterial> { return this.http.get<RawMaterial>(`${this.endpoint}/${id}`); }
  create(payload: RawMaterialPayload): Observable<RawMaterial> { return this.http.post<RawMaterial>(this.endpoint, payload); }
  update(id: number, payload: RawMaterialPayload): Observable<RawMaterial> { return this.http.put<RawMaterial>(`${this.endpoint}/${id}`, payload); }
  delete(id: number): Observable<void> { return this.http.delete<void>(`${this.endpoint}/${id}`); }
}
