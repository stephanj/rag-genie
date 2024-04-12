import {Component, OnDestroy, OnInit, Renderer2, ViewChild} from '@angular/core';
import { GenieLayoutService } from '../genie-layout-service.service';
import { LocalStorageService } from 'ngx-webstorage';
import {filter, Subscription} from 'rxjs';
import {GenieSidebarComponent} from '../authenticated/genie-sidebar.component';
import {GenieTopbarComponent} from '../shared/genie-topbar.component';
import {MenuService} from '../authenticated/genie-menu.service';
import {NavigationEnd, Router} from '@angular/router';

@Component({
  selector: 'genie-default-layout',
  templateUrl: './genie-default-layout.component.html'
})
export class GenieDefaultLayoutComponent implements OnInit, OnDestroy {

  overlayMenuOpenSubscription: Subscription;

  topbarMenuOpenSubscription: Subscription;

  menuProfileOpenSubscription: Subscription;

  menuOutsideClickListener: any;

  menuScrollListener: any;

  topbarMenuOutsideClickListener: any;

  menuProfileOutsideClickListener: any;

  @ViewChild(GenieSidebarComponent) appSidebar!: GenieSidebarComponent;

  @ViewChild(GenieTopbarComponent) appTopbar!: GenieTopbarComponent;

  constructor(private menuService: MenuService,
              public layoutService: GenieLayoutService,
              private localStorageService: LocalStorageService,
              public renderer: Renderer2,
              public router: Router) {

    this.overlayMenuOpenSubscription = this.layoutService.overlayOpen$.subscribe(() => {
      this.hideTopbarMenu();

      if (!this.menuOutsideClickListener) {
        this.menuOutsideClickListener = this.renderer.listen('document', 'click', event => {
          const isOutsideClicked = !(this.appSidebar.el.nativeElement.isSameNode(event.target) || this.appSidebar.el.nativeElement.contains(event.target)
            || this.appTopbar.menuButton.nativeElement.isSameNode(event.target) || this.appTopbar.menuButton.nativeElement.contains(event.target));
          if (isOutsideClicked) {
            this.hideMenu();
          }
        });
      }

      if ((this.layoutService.isHorizontal() || this.layoutService.isSlim()|| this.layoutService.isSlimPlus()) && !this.menuScrollListener) {
        this.menuScrollListener = this.renderer.listen(this.appSidebar.menuContainer.nativeElement, 'scroll', _ => {
          if (this.layoutService.isDesktop()) {
            this.hideMenu();
          }
        });
      }

      if (this.layoutService.state.staticMenuMobileActive) {
        this.blockBodyScroll();
      }
    });

    this.topbarMenuOpenSubscription = this.layoutService.topbarMenuOpen$.subscribe(() => {
      if (!this.topbarMenuOutsideClickListener) {
        this.topbarMenuOutsideClickListener = this.renderer.listen('document', 'click', event => {
          const isOutsideClicked = !(this.appTopbar.el.nativeElement.isSameNode(event.target) || this.appTopbar.el.nativeElement.contains(event.target)
            || this.appTopbar.mobileMenuButton.nativeElement.isSameNode(event.target) || this.appTopbar.mobileMenuButton.nativeElement.contains(event.target));
          if (isOutsideClicked) {
            this.hideTopbarMenu();
          }
        });
      }

      if (this.layoutService.state.staticMenuMobileActive) {
        this.blockBodyScroll();
      }
    });

    this.menuProfileOpenSubscription = this.layoutService.menuProfileOpen$.subscribe(() => {
      this.hideMenu();

      if (!this.menuProfileOutsideClickListener) {
        this.menuProfileOutsideClickListener = this.renderer.listen('document', 'click', event => {
          const isOutsideClicked = !(this.appSidebar.menuProfile.el.nativeElement.isSameNode(event.target) || this.appSidebar.menuProfile.el.nativeElement.contains(event.target));
          if (isOutsideClicked) {
            this.hideMenuProfile();
          }
        });
      }
    });

    this.router.events.pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.hideMenu();
        this.hideTopbarMenu();
        this.hideMenuProfile();
      });
  }

  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }

  blockBodyScroll(): void {
    if (document.body.classList) {
      document.body.classList.add('blocked-scroll');
    }
    else {
      document.body.className += ' blocked-scroll';
    }
  }

  unblockBodyScroll(): void {
    if (document.body.classList) {
      document.body.classList.remove('blocked-scroll');
    }
    else {
      document.body.className = document.body.className.replace(new RegExp('(^|\\b)' +
        'blocked-scroll'.split(' ').join('|') + '(\\b|$)', 'gi'), ' ');
    }
  }

  hideMenu(): void {
    this.layoutService.state.overlayMenuActive = false;
    this.layoutService.state.staticMenuMobileActive = false;
    this.layoutService.state.menuHoverActive = false;
    this.menuService.reset();

    if (this.menuOutsideClickListener) {
      this.menuOutsideClickListener();
      this.menuOutsideClickListener = null;
    }

    if (this.menuScrollListener) {
      this.menuScrollListener();
      this.menuScrollListener = null;
    }
    this.unblockBodyScroll();
  }

  hideTopbarMenu(): void {
    this.layoutService.state.topbarMenuActive = false;

    if (this.topbarMenuOutsideClickListener) {
      this.topbarMenuOutsideClickListener();
      this.topbarMenuOutsideClickListener = null;
    }
  }

  hideMenuProfile(): void {
    this.layoutService.state.menuProfileActive = false;

    if (this.menuProfileOutsideClickListener) {
      this.menuProfileOutsideClickListener();
      this.menuProfileOutsideClickListener = null;
    }
  }

  get containerClass() {
    const fontGroup = this.localStorageService.retrieve('fontGroup');
    if (fontGroup) {
      this.layoutService.config.fontGroup = fontGroup;
    }

    const menuMode = this.localStorageService.retrieve('menuMode');
    if (menuMode) {
      this.layoutService.config.menuMode = menuMode;
    }

    const menuProfilePosition = this.localStorageService.retrieve('menuProfilePosition');
    if (menuProfilePosition) {
      this.layoutService.config.menuProfilePosition = menuProfilePosition;
    }

    const scale = this.localStorageService.retrieve('scale');
    if (scale) {
      this.layoutService.config.scale = scale;
    }

    const colorScheme = this.localStorageService.retrieve('colorScheme');
    if (colorScheme) {
      this.layoutService.config.colorScheme = colorScheme;
    }

    const styleClass: {[key: string]: any} = {
      'layout-overlay': this.layoutService.config.menuMode === 'overlay',
      'layout-static': this.layoutService.config.menuMode === 'static',
      'layout-slim': this.layoutService.config.menuMode === 'slim',
      'layout-slim-plus': this.layoutService.config.menuMode === 'slim-plus',
      'layout-horizontal': this.layoutService.config.menuMode === 'horizontal',
      'layout-reveal': this.layoutService.config.menuMode === 'reveal',
      'layout-drawer': this.layoutService.config.menuMode === 'drawer',
      'p-input-filled': this.layoutService.config.inputStyle === 'filled',
      'p-ripple-disabled': !this.layoutService.config.ripple,
      'layout-static-inactive': this.layoutService.state.staticMenuDesktopInactive && this.layoutService.config.menuMode === 'static',
      'layout-overlay-active': this.layoutService.state.overlayMenuActive,
      'layout-mobile-active': this.layoutService.state.staticMenuMobileActive,
      'layout-topbar-menu-active': this.layoutService.state.topbarMenuActive,
      'layout-menu-profile-active': this.layoutService.state.menuProfileActive,
      'layout-sidebar-active': this.layoutService.state.sidebarActive,
      'layout-sidebar-anchored': this.layoutService.state.anchored
    };

    styleClass['layout-topbar-' + this.layoutService.config.topbarTheme] = true;
    styleClass['layout-menu-' + this.layoutService.config.menuTheme] = true;
    styleClass['layout-menu-profile-' + this.layoutService.config.menuProfilePosition] = true;
    return styleClass;
  }

  ngOnDestroy(): void {
    if (this.overlayMenuOpenSubscription) {
      this.overlayMenuOpenSubscription.unsubscribe();
    }

    if (this.menuOutsideClickListener) {
      this.menuOutsideClickListener();
    }
  }

}
