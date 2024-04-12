import { Component, ElementRef, ViewChild } from '@angular/core';
import { GenieLayoutService } from '../genie-layout-service.service';
import { JhiEventManager } from '@ng-jhipster/service';

@Component({
  selector: 'genie-topbar',
  templateUrl: './genie-topbar.component.html',
  styleUrls: ['./genie-topbar.component.scss']
})
export class GenieTopbarComponent {

  @ViewChild('menuButton') menuButton!: ElementRef;

  @ViewChild('mobileMenuButton') mobileMenuButton!: ElementRef;

  @ViewChild('searchInput') searchInput!: ElementRef;

  constructor(public layoutService: GenieLayoutService,
              protected eventManager: JhiEventManager,
              public el: ElementRef) {
  }

  onMenuButtonClick() {
    this.layoutService.onMenuToggle();
  }

  onMobileTopbarMenuButtonClick() {
    this.layoutService.onTopbarMenuToggle();
  }
}
