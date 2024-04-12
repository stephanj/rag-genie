import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { NgJhipsterModule } from '@ng-jhipster/ng-jhipster.module';
import { GenieAppRoutingModule } from './app-routing.module';
import { GenieEntityModule } from './entities/entity.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { InputTextModule } from 'primeng/inputtext';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { GenieSharedModule } from './shared/shared.module';
import { GenieCoreModule } from './core/core.module';
import { GenieHomeModule } from './home/home.module';
import { LocalStorageService, NgxWebstorageModule } from 'ngx-webstorage';
import { GenieCustomModule } from './rag/custom.module';
import { ErrorComponent } from './layouts/error/error.component';
import { GenieTopbarComponent } from './layouts/shared/genie-topbar.component';
import { GenieLayoutService } from './layouts/genie-layout-service.service';
import { GenieLayoutModule } from './layouts/genie-layout-module';
import {GenieDefaultLayoutComponent} from './layouts/anonymous/genie-default-layout.component';

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    NgxWebstorageModule.forRoot({ prefix: 'genie', separator: '-' }),
    NgJhipsterModule.forRoot({
      // set below to true to make alerts look like toast
      alertAsToast: false,
      alertTimeout: 5000,
      i18nEnabled: true,
      defaultI18nLang: 'en'
    }),
    GenieSharedModule.forRoot(),
    GenieCoreModule,
    GenieHomeModule,
    ScrollPanelModule,
    GenieEntityModule,
    GenieLayoutModule,
    GenieCustomModule,
    GenieAppRoutingModule,
    InputTextModule
  ],
  declarations: [ErrorComponent],
  providers: [GenieTopbarComponent],
  bootstrap: [GenieDefaultLayoutComponent]
})
export class AppModule {
  constructor(
    protected localStorageService: LocalStorageService,
    protected layoutService: GenieLayoutService) {
    const colorSchemer = this.localStorageService.retrieve('colorScheme');
    if (colorSchemer) {
      this.layoutService.onColorSchemeChange(colorSchemer);
    }
  }
}
