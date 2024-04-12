import { JhiResolvePagingParams } from '@ng-jhipster/public_api';
import { ApiKeysComponent } from './api-keys.component';

import type { Routes } from '@angular/router';

export const apiKeysRoute: Routes = [
  {
    path: '',
    component: ApiKeysComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams,
    },
    data: {
      defaultSort: 'id,asc',
      pageTitle: 'API Keys',
      breadcrumb: 'API Keys'
    }
  }
];
