import { Routes } from '@angular/router';
import { ChainOfDensityComponent } from './chain-of-density.component';

export const RefinementRoute: Routes = [
    {
        path: '',
        component: ChainOfDensityComponent,
        data: {
            pageTitle: 'Chain-of-Density',
            breadcrumb: 'Chain-of-Density'
        }
    }
];
