import { Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import { appConfig} from '../../../environments/environment';
import { AbstractDateEntityService} from '../../shared/crud/abstract-date-entity.service';
import {IEmbeddingModel} from '../../shared/model/embedding-model.model';
import {Observable} from 'rxjs';

@Injectable({providedIn: 'root'})
export class EmbeddingModelService extends AbstractDateEntityService<IEmbeddingModel, number> {

  public resourceUrl = appConfig.serverApiUrl + '/api/embedding-model';

  constructor(protected http: HttpClient) {
    super(http);
  }

  usedModels(): Observable<HttpResponse<IEmbeddingModel[]>> {
    return this.http.get<IEmbeddingModel[]>(`${this.resourceUrl}/used`, {observe: 'response'});
  }
}
