import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ResponseSummary } from '../chain-of-density/chain-of-density.component';

@Injectable({
  providedIn: 'root'
})
export class SummaryService {

  constructor(protected http: HttpClient) {
  }

  basicSummarization(modelId: number, temperature: number, chunks: string[]) {
    return this.http.post<string[]>(`api/summary/basic/${modelId}/${temperature}`, chunks);
  }

  codSummarization(modelId: number, temperature: number, chunks: string[]) {
    return this.http.post<ResponseSummary[]>(`api/summary/cod/${modelId}/${temperature}`, chunks);
  }
}
