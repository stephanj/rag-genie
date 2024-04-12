import { Routes } from '@angular/router';
import { SummaryComponent } from './summary.component';

export const RefinementRoute: Routes = [
    {
        path: '',
        component: SummaryComponent,
        data: {
            pageTitle: 'Summarize Documents',
            breadcrumb: 'Summarize Documents'
        }
    }
];
