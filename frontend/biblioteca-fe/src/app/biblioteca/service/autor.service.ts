import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AutorModel } from '../model/autor.model';

@Injectable({
  providedIn: 'root'
})
export class AutorService {
  private apiUrl = 'http://localhost:8080/autores';

  constructor(private http: HttpClient) { }

  getAutores(page: number = 0, size: number = 20, sort: string[] = [], nome:string = ''): Observable<any> {
    const params: any = { page, size, sort, nome};
    return this.http.get<any>(this.apiUrl + '/search/by-nome', { params });
  }

  getAutorById(id: number): Observable<AutorModel> {
    return this.http.get<AutorModel>(`${this.apiUrl}/${id}`);
  }

  createAutor(autor: AutorModel): Observable<AutorModel> {
    return this.http.post<AutorModel>(this.apiUrl, autor);
  }

  updateAutor(id: number, autor: AutorModel): Observable<AutorModel> {
    return this.http.put<AutorModel>(`${this.apiUrl}/${id}`, autor);
  }

  deleteAutor(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  patchAutor(id: number, autor: Partial<AutorModel>): Observable<AutorModel> {
    return this.http.patch<AutorModel>(`${this.apiUrl}/${id}`, autor);
  }
}
