import { Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import { GenieMenuProfileComponent } from './genie-menu-profile.component';
import { GenieLayoutService } from '../genie-layout-service.service';

@Component({
  selector: 'genie-sidebar',
  templateUrl: './genie-sidebar.component.html',
  styleUrls: ['./genie-sidebar.component.scss']
})
export class GenieSidebarComponent implements OnDestroy {

  timeout: any = null;

  @ViewChild(GenieMenuProfileComponent) menuProfile!: GenieMenuProfileComponent;

  @ViewChild('menuContainer') menuContainer!: ElementRef;

  constructor(public layoutService: GenieLayoutService, public el: ElementRef) {}

  resetOverlay() {
    if(this.layoutService.state.overlayMenuActive) {
      this.layoutService.state.overlayMenuActive = false;
    }
  }

  get menuProfilePosition(): string {
    return this.layoutService.config.menuProfilePosition;
  }

  onMouseEnter() {
    if (!this.layoutService.state.anchored) {
      if (this.timeout) {
        clearTimeout(this.timeout);
        this.timeout = null;
      }
      this.layoutService.state.sidebarActive = true;
    }
  }

  onMouseLeave() {
    if (!this.layoutService.state.anchored) {
      if (!this.timeout) {
        this.timeout = setTimeout(() => this.layoutService.state.sidebarActive = false, 300);
      }
    }
  }

  anchor() {
    this.layoutService.state.anchored = !this.layoutService.state.anchored;
  }

  ngOnDestroy() {
    this.resetOverlay();
  }

}
