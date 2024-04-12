import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export type MenuMode = 'static' | 'overlay' | 'horizontal' | 'slim' | 'slim-plus' | 'reveal' | 'drawer';

export type ColorScheme = 'light' | 'dark';

export interface AppConfig {
  inputStyle: string;
  colorScheme: ColorScheme;
  componentTheme: string;
  ripple: boolean;
  menuMode: MenuMode;
  scale: number;
  menuTheme: string;
  topbarTheme: string;
  menuProfilePosition: string;
  fontGroup: string;
}

interface LayoutState {
  staticMenuMobileActive: boolean;
  overlayMenuActive: boolean;
  staticMenuDesktopInactive: boolean;
  configSidebarVisible: boolean;
  menuHoverActive: boolean;
  rightMenuActive: boolean;
  topbarMenuActive: boolean;
  menuProfileActive: boolean;
  sidebarActive: boolean;
  anchored: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class GenieLayoutService {

  config: AppConfig = {
    ripple: true,
    inputStyle: 'outlined',
    menuMode: 'static',
    colorScheme: 'light',
    componentTheme: 'indigo',
    scale: 14,
    menuTheme: 'dark',
    topbarTheme: 'indigo',
    menuProfilePosition: 'end',
    fontGroup: 'roboto'
  };

  state: LayoutState = {
    staticMenuDesktopInactive: false,
    overlayMenuActive: false,
    configSidebarVisible: false,
    staticMenuMobileActive: false,
    menuHoverActive: false,
    rightMenuActive: false,
    topbarMenuActive: false,
    menuProfileActive: true,
    sidebarActive: false,
    anchored: false
  };

  private configUpdate = new Subject<AppConfig>();

  private overlayOpen = new Subject<any>();

  private topbarMenuOpen = new Subject<any>();

  private menuProfileOpen = new Subject<any>();

  overlayOpen$ = this.overlayOpen.asObservable();

  topbarMenuOpen$ = this.topbarMenuOpen.asObservable();

  menuProfileOpen$ = this.menuProfileOpen.asObservable();

  onMenuToggle(): void {
    if (this.isOverlay()) {
      this.state.overlayMenuActive = !this.state.overlayMenuActive;

      if (this.state.overlayMenuActive) {
        this.overlayOpen.next(null);
      }
    }

    if (this.isDesktop()) {
      this.state.staticMenuDesktopInactive = !this.state.staticMenuDesktopInactive;
    }
    else {
      this.state.staticMenuMobileActive = !this.state.staticMenuMobileActive;

      if (this.state.staticMenuMobileActive) {
        this.overlayOpen.next(null);
      }
    }
  }

  onTopbarMenuToggle(): void {
    this.state.topbarMenuActive = !this.state.topbarMenuActive;
    if (this.state.topbarMenuActive) {
      this.topbarMenuOpen.next(null);
    }
  }

  onOverlaySubmenuOpen(): void {
    this.overlayOpen.next(null);
  }

  showConfigSidebar(): void {
    this.state.configSidebarVisible = true;
  }

  isOverlay() {
    return this.config.menuMode === 'overlay';
  }

  isDesktop() {
    return window.innerWidth > 991;
  }

  isSlim() {
    return this.config.menuMode === 'slim';
  }

  isSlimPlus() {
    return this.config.menuMode === 'slim-plus';
  }

  isHorizontal() {
    return this.config.menuMode === 'horizontal';
  }

  isMobile() {
    return !this.isDesktop();
  }

  onConfigUpdate() {
    this.configUpdate.next(this.config);
  }

  isRightMenuActive(): boolean {
    return this.state.rightMenuActive;
  }

  openRightSidebar(): void {
    this.state.rightMenuActive = true;
  }

  onMenuProfileToggle() {
    this.state.menuProfileActive = !this.state.menuProfileActive;
    if (this.state.menuProfileActive && this.isHorizontal() && this.isDesktop()) {
      this.menuProfileOpen.next(null);
    }
  }

  replaceThemeLink(href: string, onComplete: () => void) {
    const id = 'theme-link';
    const themeLink = <HTMLLinkElement>document.getElementById(id);
    const cloneLinkElement = <HTMLLinkElement>themeLink.cloneNode(true);

    cloneLinkElement.setAttribute('href', href);
    cloneLinkElement.setAttribute('id', id + '-clone');

    themeLink.parentNode!.insertBefore(cloneLinkElement, themeLink.nextSibling);

    cloneLinkElement.addEventListener('load', () => {
      themeLink.remove();
      cloneLinkElement.setAttribute('id', id);
      onComplete();
    });
  }

  replaceFontLink(href: string, onComplete: () => void) {
    const id = 'font-link';
    const fontLink = <HTMLLinkElement>document.getElementById(id);
    if (!fontLink) return; // Make sure the element exists

    const cloneFontElement = <HTMLLinkElement>fontLink.cloneNode(true);

    // Determine the new href
    // cloneFontElement.getAttribute('href');

    cloneFontElement.setAttribute('href', href);
    cloneFontElement.setAttribute('id', id + '-clone');

    fontLink.parentNode!.insertBefore(cloneFontElement, fontLink.nextSibling);

    cloneFontElement.addEventListener('load', () => {
      fontLink.remove();
      cloneFontElement.setAttribute('id', id);
      onComplete();
    });
  }

  onColorSchemeChange(colorScheme: ColorScheme) {
    const themeLink = <HTMLLinkElement>document.getElementById('theme-link');
    const themeLinkHref = themeLink.getAttribute('href');
    const currentColorScheme = 'theme-' + this.config.colorScheme;
    const newColorScheme = 'theme-' + colorScheme;
    const newHref = themeLinkHref!.replace(currentColorScheme, newColorScheme);

    this.replaceThemeLink(newHref, () => {
      this.config.colorScheme = colorScheme;
      if (colorScheme === 'dark') {
        this.config.menuTheme = 'dark';
      }
      this.onConfigUpdate();
    });
  }

  onFontGroupChange(newFontGroup: string) {
    const fontLink = <HTMLLinkElement>document.getElementById('font-link');
    const fontLinkHref = fontLink.getAttribute('href');
    let newHref;
    if (newFontGroup.includes('1')) {
      newHref = fontLinkHref!.replace('2', '1');
    } else {
      newHref = fontLinkHref!.replace('1', '2');
    }
    this.replaceFontLink(newHref, () => {
      this.config.fontGroup = newFontGroup;
      this.onConfigUpdate();
    });
  }

  /**
   * Reset the layout state to the anonmyous state
   */
  reset(): void {
    this.topbarMenuOpen.next(null);
    this.menuProfileOpen.next(null);
    this.overlayOpen.next(null);

    if (this.isRightMenuActive()) {
      this.state.rightMenuActive = false;
    }
  }
}
