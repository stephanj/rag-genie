import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { appConfig } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ChatbotService {
  protected resourceUrl = appConfig.serverApiUrl + '/api/chat';
  constructor(protected http: HttpClient) { }

  chat(param: any): Observable<string> {
    return this.http.post(this.resourceUrl, param, { responseType: 'text' });
  }
}
