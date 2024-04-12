import { ActivatedRouteSnapshot, ResolveFn, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { inject } from '@angular/core';

export function GenericResolveFn<T>(
  serviceClass: new (...args: any[]) => { find: (id: number) => Observable<T> },
  newInstance: () => T  // Factory function to create a new instance of T
): ResolveFn<T> {
  return (
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
    service: any = inject(serviceClass)
  ): Observable<T> => {
    const id = route.paramMap.get('id');
    const idNumber = id !== null ? +id : null;

    if (idNumber === null) {
      // Return an Observable of a new instance of T
      return of(newInstance());
    }

    return service.find(idNumber).pipe(
      switchMap((item: T) => {
        if (item) {
          return of(item);
        } else {
          // Item not found, return a new instance of T
          return of(newInstance());
        }
      })
    );
  };
}
