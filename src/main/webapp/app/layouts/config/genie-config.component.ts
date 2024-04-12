import { Component, Input, OnInit } from '@angular/core';
import { GenieLayoutService, ColorScheme, MenuMode } from '../genie-layout-service.service';
import { MenuService } from '../authenticated/genie-menu.service';
import { LocalStorageService } from 'ngx-webstorage';

@Component({
  selector: 'genie-config',
  templateUrl: './genie-config.component.html'
})
export class GenieConfigComponent implements OnInit {

  @Input() minimal: boolean = false;

  scales: number[] = [12,13,14,15,16];

  showSummary: boolean = false;

  constructor(public layoutService: GenieLayoutService,
              public menuService: MenuService,
              public localStorageService: LocalStorageService) { }

  ngOnInit() {
    this.showSummary = this.localStorageService.retrieve('showSummary');
  }

  get visible(): boolean {
    return this.layoutService.state.configSidebarVisible;
  }

  set visible(_val: boolean) {
    this.layoutService.state.configSidebarVisible = _val;
  }

  get scale(): number {
    return this.layoutService.config.scale;
  }

  set scale(_val: number) {
    this.layoutService.config.scale = _val;
  }

  get menuMode(): MenuMode {
    return this.layoutService.config.menuMode;
  }

  set menuMode(_val: MenuMode) {
    this.localStorageService.store('menuMode', _val);
    this.layoutService.config.menuMode = _val;
    if (this.layoutService.isSlim() || this.layoutService.isHorizontal()) {
      this.menuService.reset();
    }
  }

  get menuProfilePosition(): string {
    return this.layoutService.config.menuProfilePosition;
  }

  set menuProfilePosition(_val: string) {
    this.localStorageService.store('menuProfilePosition', _val);
    this.layoutService.config.menuProfilePosition = _val;
    if (this.layoutService.isSlimPlus() || this.layoutService.isSlim() || this.layoutService.isHorizontal()) {
      this.menuService.reset();
    }
  }

  get fontGroup(): string {
    return this.layoutService.config.fontGroup;
  }

  set fontGroup(_val: string) {
    this.localStorageService.store('fontGroup', _val);
    this.changeFont(_val);
  }

  get colorScheme(): ColorScheme {
    return this.layoutService.config.colorScheme;
  }

  set colorScheme(_val: ColorScheme) {
    this.localStorageService.store('colorScheme', _val);
    this.changeColorScheme(_val);
  }

  onConfigButtonClick() {
    this.layoutService.showConfigSidebar();
  }

  changeColorScheme(colorScheme: ColorScheme) {
    this.layoutService.onColorSchemeChange(colorScheme);
  }

  changeFont(fontGroup: string) {
    this.layoutService.onFontGroupChange(fontGroup);
  }

  decrementScale() {
    this.scale--;
    this.applyScale();
    this.localStorageService.store('scale', this.scale);
  }

  incrementScale() {
    this.scale++;
    this.applyScale();
    this.localStorageService.store('scale', this.scale);
  }

  applyScale() {
    document.documentElement.style.fontSize = this.scale + 'px';
  }
}
