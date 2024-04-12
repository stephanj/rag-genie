import { Routes } from '@angular/router';
import { TextSplitterComponent } from './text-splitter.component';

export const TextSplitterRoute: Routes = [
    {
        path: '',
        component: TextSplitterComponent,
        data: {
            pageTitle: 'Text Splitting',
            breadcrumb: 'Text Splitting'
        }
    }
];
