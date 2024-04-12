import { Component, ElementRef, OnInit } from '@angular/core';
import { trigger, transition, style, animate } from '@angular/animations';
import { GenieLayoutService } from '../genie-layout-service.service';
import { Account } from '../../shared/model/account.model';

@Component({
  selector: 'genie-menu-profile',
  templateUrl: './genie-menu-profile.component.html',
  animations: [
    trigger('menu', [
      transition('void => inline', [
        style({ height: 0 }),
        animate('400ms cubic-bezier(0.86, 0, 0.07, 1)', style({ opacity: 1, height: '*' })),
      ]),
      transition('inline => void', [
        animate('400ms cubic-bezier(0.86, 0, 0.07, 1)', style({ opacity: 0, height: '0' }))
      ]),
      transition('void => overlay', [
        style({ opacity: 0, transform: 'scaleY(0.8)' }),
        animate('.12s cubic-bezier(0, 0, 0.2, 1)')
      ]),
      transition('overlay => void', [
        animate('.1s linear', style({ opacity: 0 }))
      ])
    ])
  ]
})
export class GenieMenuProfileComponent {

  account?: Account;

  readonly ANONYMOUS_IMG_LINK = '/assets/images/logo.png';

  constructor(public layoutService: GenieLayoutService,
              public el: ElementRef) { }

  toggleMenu() {
    this.layoutService.onMenuProfileToggle();
  }

  get isHorizontal() {
    return this.layoutService.isHorizontal() && this.layoutService.isDesktop();
  }

  get menuProfileActive(): boolean {
    return this.layoutService.state.menuProfileActive;
  }

  get menuProfilePosition(): string {
    return this.layoutService.config.menuProfilePosition;
  }

  get isTooltipDisabled(): boolean {
    return !this.layoutService.isSlim();
  }

  logout(): void {
  }
}
