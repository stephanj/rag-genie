import {Route} from '@angular/router';
import { HomeComponent } from './home.component';

export const HOME_ROUTE: Route = {
  path: '',
  children: [
    {
      path: '',
      component: HomeComponent,
      children:[],
      data: {
        pageTitle: 'Dashboard',
        breadcrumb: 'Dashboard',
      }
    }
  ]
};
