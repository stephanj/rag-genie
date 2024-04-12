import { Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { AbstractDateEntityService } from '../../../shared/crud/abstract-date-entity.service';
import { IEvaluation } from '../../../shared/model/evaluation.model';
import { appConfig } from '../../../../environments/environment';

@Injectable({providedIn: 'root'})
export class EvaluationService extends AbstractDateEntityService<IEvaluation, number> {

  public resourceUrl = appConfig.serverApiUrl + '/api/evaluation';

  constructor(protected http: HttpClient) {
    super(http);
  }

  startEvaluation(params: HttpParams): any {
    return this.http.post(`${this.resourceUrl}/start`, params);
  }
}
