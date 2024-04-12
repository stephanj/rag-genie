import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SimilarityRoute } from './similarity.route';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { ButtonModule } from 'primeng/button';
import { GenieSharedModule } from '../../../shared/shared.module';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { MessageService } from 'primeng/api';
import { SimilarityComponent } from './similarity.component';
import { SplitterFormComponent } from '../../../shared/splitter-form/splitter-form.component';
import { ContentListComponent } from '../../../shared/content-list/content-list.component';
import { DialogModule } from 'primeng/dialog';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { PanelModule } from 'primeng/panel';
import { InputNumberModule } from 'primeng/inputnumber';
import { TableModule } from 'primeng/table';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TooltipModule } from 'primeng/tooltip';
import { ToastModule } from 'primeng/toast';
import {CheckboxModule} from 'primeng/checkbox';

const ENTITY_STATES = [...SimilarityRoute];

@NgModule({
    imports: [
        AutoCompleteModule,
        ButtonModule,
        GenieSharedModule,
        DropdownModule,
        InputTextModule,
        InputTextareaModule,
        RouterModule.forChild(ENTITY_STATES),
        SplitterFormComponent,
        ContentListComponent,
        DialogModule,
        ScrollPanelModule,
        PanelModule,
        InputNumberModule,
        TableModule,
        ConfirmDialogModule,
        TooltipModule,
        ToastModule,
        CheckboxModule
    ],
  declarations: [SimilarityComponent],
  providers: [MessageService],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SimilarityModule {
}
