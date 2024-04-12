import {InjectionToken, NgModule} from '@angular/core';
import {ActivatedRouteSnapshot, RouterModule} from '@angular/router';
import {errorRoute} from './layouts/error/error.route';
import {DEBUG_INFO_ENABLED} from './app.constants';
import {HomeComponent} from './home/home.component';

const LAYOUT_ROUTES = [...errorRoute];

const externalUrlProvider = new InjectionToken('externalUrlRedirectResolver');

@NgModule({
  providers: [
    {
      provide: externalUrlProvider,
      useValue(route: ActivatedRouteSnapshot): void {
        const externalUrl = route.paramMap.get('externalUrl');
        if (externalUrl != null) {
          window.open(externalUrl, '_self');
        }
      }
    }
  ],
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'externalRedirect',
          children: [],
          resolve: {
            url: externalUrlProvider
          },
          component: HomeComponent
        },
        ...LAYOUT_ROUTES
      ],
      {
        useHash: true,
        enableTracing: DEBUG_INFO_ENABLED,
        anchorScrolling: 'enabled',
        scrollPositionRestoration: 'enabled'
      }
    )
  ],
  exports: [RouterModule]
})
export class GenieAppRoutingModule {}
