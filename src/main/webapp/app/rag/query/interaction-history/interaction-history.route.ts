import { Routes } from '@angular/router';
import { InteractionHistoryComponent } from './interaction-history.component';

export const GenieInteractionRoute: Routes = [
    {
        path: '',
        component: InteractionHistoryComponent,
        data: {
            pageTitle: 'Interaction History',
            breadcrumb: 'Interaction History'
        }
    }
];
