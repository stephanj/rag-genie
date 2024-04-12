import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import {Observable} from 'rxjs';
import { IContent } from '../../../shared/model/content.model';

export class TokenInfo{
  constructor(public inputTokens: number,
              public outputTokens: number,
              public inputCost: number,
              public outputCost: number) {}
}

@Injectable({
  providedIn: 'root'
})
export class ContentService {

  constructor(protected http: HttpClient) {
  }

  getTotalTokens(): Observable<number> {
    return this.http.get<number>('api/content/tokens');
  }

  deleteById(id: number): Observable<IContent> {
    return this.http.delete(`api/content/${id}`);
  }

  deleteAll(): Observable<IContent> {
    return this.http.delete('api/content');
  }

  analyzeContent(param: HttpParams): Observable<HttpResponse<string>> {
    return this.http.post<string>(`api/analyze/content`, param,
      {responseType: 'text' as 'json', observe: 'response'});
  }

  getAnalysisCost(param: HttpParams): Observable<HttpResponse<TokenInfo>> {
    return this.http.post<TokenInfo>(`api/analyze/cost`, param, { observe: 'response'});
  }

  getDocumentsCount(): Observable<HttpResponse<any>> {
    return this.http.head<number>('api/vector-document', { observe: 'response' });
  }
}
