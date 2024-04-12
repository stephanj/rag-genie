import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import type { MenuChangeEvent } from './menuchangeevent';

@Injectable({
  providedIn: 'root'
})
export class MenuService {

  private menuSource = new Subject<MenuChangeEvent>();
  private resetSource = new Subject();

  menuSource$ = this.menuSource.asObservable();
  resetSource$ = this.resetSource.asObservable();

  onMenuStateChange(event: MenuChangeEvent): void {
    this.menuSource.next(event);
  }

  reset(): void {
    this.resetSource.next(true);
  }
}
