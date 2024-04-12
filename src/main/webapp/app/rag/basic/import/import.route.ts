import { Routes } from '@angular/router';
import { ImportComponent } from './import.component';

export const ImportRoute: Routes = [
    {
        path: '',
        component: ImportComponent,
        data: {
            pageTitle: 'Import Data',
            breadcrumb: 'Import Data'
        }
    }
];
