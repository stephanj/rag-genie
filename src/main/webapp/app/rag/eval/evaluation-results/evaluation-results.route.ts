import { JhiResolvePagingParams } from '@ng-jhipster/public_api';
import { EvaluationResultsComponent } from './evaluation-results.component';
import { EvaluationResultsViewComponent } from './evaluation-results-view.component';
import type { Routes } from '@angular/router';
import { EvaluationResultService } from './evaluation-result.service';
import { EvaluationResult, IEvaluationResult } from '../../../shared/model/evaluation-result.model';
import { GenericResolveFn } from '../../../shared/resolver/generic-resolver';

function createNewEvaluationResult(): IEvaluationResult {
  return new EvaluationResult();
}

export const evaluationResultsRoute: Routes = [
  {
    path: '',
    component: EvaluationResultsComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
    },
    data: {
      defaultSort: 'id,asc',
      pageTitle: 'Evaluations',
      breadcrumb: 'Evaluations'
    }
  },
  {
    path: ':id/view',
    component: EvaluationResultsViewComponent,
    resolve: {
      evaluationResult: GenericResolveFn<IEvaluationResult>(EvaluationResultService, createNewEvaluationResult),
    },
    data: {
      pageTitle: 'Edit Evaluation',
      breadcrumb: 'Edit Evaluation'
    }
  },
];
