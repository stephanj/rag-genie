import { Routes } from '@angular/router';
import { SimilarityComponent } from './similarity.component';

export const SimilarityRoute: Routes = [
    {
        path: '',
        component: SimilarityComponent,
        data: {
            pageTitle: 'Similarity Search',
            breadcrumb: 'Similarity Search'
        }
    }
];
