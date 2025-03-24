import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AssuntoModel } from '../model/assunto.model';

@Injectable({
  providedIn: 'root'
})
export class AssuntoService {
  private apiUrl = 'http://localhost:8080/assuntos';

  constructor(private http: HttpClient) { }

  getAssuntos(page: number = 0, size: number = 20, sort: string[] = [], descricao:string = ''): Observable<any> {
    const params: any = { page, size, sort, descricao };
    return this.http.get<any>(this.apiUrl + '/search/by-descricao', { params });
  }

  getAssuntoById(id: number): Observable<AssuntoModel> {
    return this.http.get<AssuntoModel>(`${this.apiUrl}/${id}`);
  }

  createAssunto(assunto: AssuntoModel): Observable<AssuntoModel> {
    return this.http.post<AssuntoModel>(this.apiUrl, assunto);
  }

  updateAssunto(id: number, assunto: AssuntoModel): Observable<AssuntoModel> {
    return this.http.put<AssuntoModel>(`${this.apiUrl}/${id}`, assunto);
  }

  deleteAssunto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  patchAssunto(id: number, assunto: Partial<AssuntoModel>): Observable<AssuntoModel> {
    return this.http.patch<AssuntoModel>(`${this.apiUrl}/${id}`, assunto);
  }
}
