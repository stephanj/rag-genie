import {LOCALE_ID, NgModule} from '@angular/core';
import {DatePipe, registerLocaleData} from '@angular/common';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {Title} from '@angular/platform-browser';
import {FaIconLibrary} from '@fortawesome/angular-fontawesome';
import {CookieService} from 'ngx-cookie-service';
import {NgxWebstorageModule} from 'ngx-webstorage';
import locale from '@angular/common/locales/en';
import {NgbDatepickerConfig} from '@ng-bootstrap/ng-bootstrap';
import * as moment from 'moment';
import {ErrorHandlerInterceptor} from '../blocks/interceptor/errorhandler.interceptor';
import {NotificationInterceptor} from '../blocks/interceptor/notification.interceptor';
import {fontAwesomeIcons} from './icons/font-awesome-icons';
import {
  NgJhipsterModule,
} from '@ng-jhipster/public_api';

@NgModule({
  imports: [
    HttpClientModule,
    NgxWebstorageModule.forRoot({ prefix: 'genie', separator: '-' }),
    NgJhipsterModule.forRoot({
      // set below to true to make alerts look like toast
      alertAsToast: false,
      alertTimeout: 5000,
      i18nEnabled: true,
    }),
  ],
  providers: [
    Title,
    CookieService,
    {
      provide: LOCALE_ID,
      useValue: 'en',
    },
    DatePipe,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorHandlerInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: NotificationInterceptor,
      multi: true,
    }
  ],
})
export class GenieCoreModule {
  constructor(iconLibrary: FaIconLibrary,
              dpConfig: NgbDatepickerConfig) {
    registerLocaleData(locale);
    iconLibrary.addIcons(...fontAwesomeIcons);
    dpConfig.minDate = { year: moment().year() - 100, month: 1, day: 1 };
  }
}

