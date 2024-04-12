import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ToastModule } from 'primeng/toast';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ProgressBarModule } from 'primeng/progressbar';
import { MessageService } from 'primeng/api';
import { GenieSharedModule } from '../../../shared/shared.module';
import { TextSplitterRoute } from './text-splitter.route';
import { TextSplitterComponent } from './text-splitter.component';
import { ContentListComponent } from '../../../shared/content-list/content-list.component';
import { SplitterFormComponent } from '../../../shared/splitter-form/splitter-form.component';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { PanelModule } from 'primeng/panel';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import {DialogModule} from 'primeng/dialog';
import {DropdownModule} from 'primeng/dropdown';

const ENTITY_STATES = [...TextSplitterRoute];

@NgModule({
  imports: [
    ButtonModule,
    GenieSharedModule,
    InputTextModule,
    InputTextareaModule,
    ProgressBarModule,
    RouterModule.forChild(ENTITY_STATES),
    TableModule,
    ToastModule,
    ContentListComponent,
    SplitterFormComponent,
    ScrollPanelModule,
    PanelModule,
    TooltipModule,
    ConfirmDialogModule,
    DialogModule,
    DropdownModule
  ],
  declarations: [TextSplitterComponent],
  providers: [MessageService],
  exports: [
    TextSplitterComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GenieTextSplitterModule {
}
