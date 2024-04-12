import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ButtonModule } from 'primeng/button';
import { PanelModule } from 'primeng/panel';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { TabViewModule } from 'primeng/tabview';
import { TooltipModule } from 'primeng/tooltip';
import { HOME_ROUTE } from './home.route';
import { GenieSharedModule } from '../shared/shared.module';
import { MatStepperModule } from '@angular/material/stepper';
import { MatButtonModule } from '@angular/material/button';
import { MessagesModule } from 'primeng/messages';
import { MessageModule } from 'primeng/message';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { StepsModule } from 'primeng/steps';
import { HomeComponent } from './home.component';

@NgModule({
  imports: [
    ButtonModule,
    CheckboxModule,
    GenieSharedModule,
    InputTextModule,
    RouterModule.forChild([HOME_ROUTE]),
    PanelModule,
    ScrollPanelModule,
    TabViewModule,
    TooltipModule,
    MatStepperModule,
    MatButtonModule,
    MessagesModule,
    MessageModule,
    FontAwesomeModule,
    StepsModule
  ],
  declarations: [HomeComponent],
  providers: [],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class GenieHomeModule {
}
