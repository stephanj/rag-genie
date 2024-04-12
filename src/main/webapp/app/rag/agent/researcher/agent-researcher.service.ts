import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AgentResearcherService {

  resourceUrl = '/api/agent-researcher';

  constructor(protected http: HttpClient) {
  }

  startResearch(data: any): Observable<HttpResponse<string>> {
    return this.http.post<string>(`${this.resourceUrl}`, data, { observe: 'response' });
  }
}
