import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {appConfig} from '../../../environments/environment';
import {UserApiKey} from '../../shared/model/api-keys.model';
import type { Observable } from 'rxjs';

@Injectable({providedIn: 'root'})
export class ApiKeysService {

  public resourceUrl = appConfig.serverApiUrl + '/api/api-keys';

  constructor(protected http: HttpClient) {
  }

  create(params: UserApiKey) {
    return this.http.post<UserApiKey>(this.resourceUrl, params);
  }

  query(req?: any) {
    return this.http.get<UserApiKey[]>(this.resourceUrl, {params: req, observe: 'response'});
  }

  checkApiKey(model: string): Observable<boolean> {
    return this.http.get<boolean>('/api/api-keys/' + model);
  }

  delete(id: number) {
    return this.http.delete(`${this.resourceUrl}/${id}`);
  }
}
