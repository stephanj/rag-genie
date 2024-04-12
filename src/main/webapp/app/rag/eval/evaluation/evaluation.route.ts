import { EvaluationService } from './evaluation.service';
import { EvaluationComponent } from './evaluation.component';
import { EvaluationUpdateComponent } from './evaluation-update.component';
import type { Routes } from '@angular/router';
import { Evaluation, IEvaluation } from '../../../shared/model/evaluation.model';
import { GenericResolveFn } from '../../../shared/resolver/generic-resolver';
import { JhiResolvePagingParams } from '@ng-jhipster/service';

function createNewEvaluation(): IEvaluation {
  return new Evaluation();
}

export const evaluationRoute: Routes = [
  {
    path: '',
    component: EvaluationComponent,
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
    path: 'new',
    component: EvaluationUpdateComponent,
    resolve: {
      evaluation: GenericResolveFn<IEvaluation>(EvaluationService, createNewEvaluation),
    },
    data: {
      pageTitle: 'Create Evaluation',
      breadcrumb: 'Create Evaluation'
    }
  },
  {
    path: ':id/edit',
    component: EvaluationUpdateComponent,
    resolve: {
      evaluation: GenericResolveFn<IEvaluation>(EvaluationService, createNewEvaluation),
    },
    data: {
      pageTitle: 'Edit Evaluation',
      breadcrumb: 'Edit Evaluation'
    }
  },
];
