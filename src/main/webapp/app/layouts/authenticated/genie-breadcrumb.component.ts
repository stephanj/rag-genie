import { Component } from '@angular/core';
import { ActivatedRouteSnapshot, NavigationEnd, Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { filter } from 'rxjs/operators';
import { JhiEventManager } from '@ng-jhipster/service';

interface Breadcrumb {
  label: string;
  url?: string;
}

@Component({
  selector: 'genie-breadcrumb',
  templateUrl: './genie-breadcrumb.component.html'
})
export class GenieBreadcrumbComponent {

  private readonly _breadcrumbs$ = new BehaviorSubject<Breadcrumb[]>([]);

  readonly breadcrumbs$ = this._breadcrumbs$.asObservable();

  constructor(private router: Router,
              private eventManager: JhiEventManager) {
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd))
      .subscribe(_ => {
          const root = this.router.routerState.snapshot.root;
          const breadcrumbs: Breadcrumb[] = [];
          this.addBreadcrumb(root, [], breadcrumbs);

          this._breadcrumbs$.next(breadcrumbs);
        });
  }

  private addBreadcrumb(route: ActivatedRouteSnapshot, parentUrl: string[], breadcrumbs: Breadcrumb[]) {
    const routeUrl = parentUrl.concat(route.url.map(url => url.path));
    const breadcrumb = route.data['breadcrumb'];
    const parentBreadcrumb = route.parent && route.parent.data ? route.parent.data['breadcrumb'] : null;

    if (breadcrumb && breadcrumb !== parentBreadcrumb) {
      const breadCrumbKey = route.data['breadcrumb'];
      let label = '';

      if (!breadCrumbKey.startsWith('home.title')) {
        label = breadCrumbKey;
        breadcrumbs.push({ label, url: '/' + routeUrl.join('/') });
      }
    }

    if (route.firstChild) {
      this.addBreadcrumb(route.firstChild, routeUrl, breadcrumbs);
    }
  }
}
