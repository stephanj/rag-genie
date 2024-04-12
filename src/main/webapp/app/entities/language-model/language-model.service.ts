import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {appConfig} from '../../../environments/environment';
import {AbstractDateEntityService} from '../../shared/crud/abstract-date-entity.service';
import { ILanguageModel } from '../../shared/model/language-model.model';

@Injectable({providedIn: 'root'})
export class LanguageModelService extends AbstractDateEntityService<ILanguageModel, number> {

  public resourceUrl = appConfig.serverApiUrl + '/api/lang-model';

  constructor(protected http: HttpClient) {
    super(http);
  }

  getOllamaModels(): any {
    return this.http.get<ILanguageModel[]>(`${this.resourceUrl}/ollama`);
  }
}
