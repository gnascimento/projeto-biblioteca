import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import { LivroModel } from '../model/livro.model';
import {LivroDTO} from '../dto/livro.dto';

@Injectable({
  providedIn: 'root'
})
export class LivroService {
  private apiUrl = 'http://localhost:8080/livros';

  constructor(private http: HttpClient) { }

  getLivros(page: number = 0, size: number = 20, sort: string[] = [], titulo: string = ''): Observable<any> {
    const params: any = { page, size, sort, titulo };
    return this.http.get<any>(this.apiUrl + '/search/by-titulo', { params });
  }

  getLivroById(id: number): Observable<LivroModel> {
    return this.http.get<LivroModel>(`${this.apiUrl}/${id}`);
  }

  createLivro(livro: LivroDTO): Observable<any> {
    return this.http.post<any>(this.apiUrl, livro);
  }

  updateLivro(id: number, livro: LivroDTO): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, livro);
  }

  deleteLivro(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  patchLivro(id: number, livro: Partial<LivroDTO>): Observable<LivroModel> {
    return this.http.patch<LivroModel>(`${this.apiUrl}/${id}`, livro);
  }

  updateLivroAssuntos(id: number, assuntos: string[] = []): Observable<void> {
    const url = `${this.apiUrl}/${id}/assuntos`;
    const headers = new HttpHeaders({ 'Content-Type': 'text/uri-list' });
    const body = assuntos.join('\n');
    return this.http.put<void>(url, body, { headers });
  }

  updateLivroAutores(id: number, autores: string[] = []): Observable<void> {
    const url = `${this.apiUrl}/${id}/autores`;
    const headers = new HttpHeaders({ 'Content-Type': 'text/uri-list' });
    const body = autores.join('\n');
    return this.http.put<void>(url, body, { headers });
  }

  getLivroAutores(id: number): Observable<any> {
    const url = `${this.apiUrl}/${id}/autores`;
    return this.http.get<any>(url);
  }

  getLivroAssuntos(id: number): Observable<any> {
    const url = `${this.apiUrl}/${id}/assuntos`;
    return this.http.get<any>(url);
  }

  exportToExcelUrl() {
    const url = `${this.apiUrl}/export/excel`;
    return url;
  }
}
