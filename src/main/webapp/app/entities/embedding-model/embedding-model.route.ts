import { EmbeddingModelComponent } from './embedding-model.component';
import type { Routes } from '@angular/router';

export const embeddingModelRoute: Routes = [
  {
    path: '',
    component: EmbeddingModelComponent,
    data: {
      pageTitle: 'Embedding Model',
      breadcrumb: 'Embedding Model'
    }
  }
];
