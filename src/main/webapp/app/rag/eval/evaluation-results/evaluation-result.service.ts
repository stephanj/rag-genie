import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import { AbstractDateEntityService } from '../../../shared/crud/abstract-date-entity.service';
import { IEvaluationResult } from '../../../shared/model/evaluation-result.model';
import { appConfig } from '../../../../environments/environment';

@Injectable({providedIn: 'root'})
export class EvaluationResultService extends AbstractDateEntityService<IEvaluationResult, number> {

  public resourceUrl = appConfig.serverApiUrl + '/api/evaluation-result';

  constructor(protected http: HttpClient) {
    super(http);
  }

  deleteAll() {
    return this.http.delete(`${this.resourceUrl}/all`);
  }
}
