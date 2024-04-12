import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {NgJhipsterModule} from '@ng-jhipster/public_api';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';

@NgModule({
  exports: [
    FormsModule,
    CommonModule,
    NgbModule,
    NgJhipsterModule,
    FontAwesomeModule,
    ReactiveFormsModule]
})
export class GenieSharedLibsModule {
}
