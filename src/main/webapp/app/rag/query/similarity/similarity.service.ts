import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DocumentResult } from '../../../shared/model/document-result.model';

@Injectable({
  providedIn: 'root'
})
export class SimilarityService {

  resourceUrl = '/api/vector-document/';

  constructor(protected http: HttpClient) {
  }

  public query(params: HttpParams): Observable<DocumentResult[]> {
    return this.http.get<DocumentResult[]>(this.resourceUrl + 'similarity', { params });
  }

  public delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.resourceUrl}/${id}`);
  }
}
