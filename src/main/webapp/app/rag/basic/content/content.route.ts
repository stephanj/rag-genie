import { Routes } from '@angular/router';
import { ContentComponent } from './content.component';
import { UpdateContentComponent } from './content-update.component';

export const GenieContentRoute: Routes = [
    {
        path: '',
        component: ContentComponent,
        data: {
            pageTitle: 'Content',
            breadcrumb: 'Content'
        }
    },
    {
      path: ':id/edit',
      component: UpdateContentComponent,
      data: {
        pageTitle: 'Edit Content',
        breadcrumb: 'Edit Content'
      }
    }
];
