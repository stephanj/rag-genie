import { Routes } from '@angular/router';
import { DocumentComponent } from './document.component';
import { DocumentUpdateComponent } from './document-update.component';

export const DocumentRoute: Routes = [
    {
        path: '',
        component: DocumentComponent,
        data: {
            pageTitle: 'Documents',
            breadcrumb: 'Documents'
        }
    },
    {
      path: ':id/:dim/edit',
      component: DocumentUpdateComponent,
      data: {
        pageTitle: 'Edit Document',
        breadcrumb: 'Edit Document'
      }
    },
];
