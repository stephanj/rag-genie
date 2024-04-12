import { JhiResolvePagingParams } from '@ng-jhipster/public_api';
import { LanguageModelService } from './language-model.service';
import { LanguageModelComponent } from './language-model.component';
import { LanguageModelUpdateComponent } from './language-model-update.component';
import { GenericResolveFn } from '../../shared/resolver/generic-resolver';
import type { Routes } from '@angular/router';
import { ILanguageModel, LanguageModel } from '../../shared/model/language-model.model';

function createNewLanguageModel(): ILanguageModel {
  return new LanguageModel();
}

export const languageModelRoute: Routes = [
  {
    path: '',
    component: LanguageModelComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
    },
    data: {
      defaultSort: 'id,asc',
      pageTitle: 'Language Model',
      breadcrumb: 'Language Model'
    }
  },
  {
    path: 'new',
    component: LanguageModelUpdateComponent,
    resolve: {
      languageModel: GenericResolveFn<ILanguageModel>(LanguageModelService, createNewLanguageModel),
    },
    data: {
      pageTitle: 'Create Language Model',
      breadcrumb: 'Create Language Model'
    }
  },
  {
    path: ':id/edit',
    component: LanguageModelUpdateComponent,
    resolve: {
      languageModel: GenericResolveFn<ILanguageModel>(LanguageModelService, createNewLanguageModel),
    },
    data: {
      pageTitle: 'Edit Language Model',
      breadcrumb: 'Edit Language Model'
    }
  },
];
