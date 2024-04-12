import { Injectable } from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import { appConfig } from '../../../../environments/environment';
import {Observable} from 'rxjs';

@Injectable({providedIn: 'root'})
export class VectorVisualizationService {

  public resourceUrl = appConfig.serverApiUrl + '/api/embeddings';

  constructor(protected http: HttpClient) {
  }

  getEmbeddings(dimension: string, contentId: number): Observable<HttpResponse<number[][]>> {
    return this.http.get<number[][]>(`${this.resourceUrl}/${dimension}/${contentId}`, { observe: 'response' });
  }
}
