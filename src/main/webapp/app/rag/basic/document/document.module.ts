import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DocumentRoute } from './document.route';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { ButtonModule } from 'primeng/button';
import { GenieSharedModule } from '../../../shared/shared.module';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { MessageService } from 'primeng/api';
import { DocumentComponent } from './document.component';
import { SplitterFormComponent } from '../../../shared/splitter-form/splitter-form.component';
import { ContentListComponent } from '../../../shared/content-list/content-list.component';
import { DialogModule } from 'primeng/dialog';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { PanelModule } from 'primeng/panel';
import { InputNumberModule } from 'primeng/inputnumber';
import { TableModule } from 'primeng/table';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DocumentUpdateComponent } from './document-update.component';
import { BadgeModule } from 'primeng/badge';
import { VectorVisualizationComponent} from '../../advanced/visualize/vector-visualization.component';
import { ToastModule} from 'primeng/toast';
import { TooltipModule} from 'primeng/tooltip';
import { TabViewModule} from 'primeng/tabview';
import { AbstractDocumentComponent} from './abstract-document.component';
import {ProgressBarModule} from 'primeng/progressbar';

const ENTITY_STATES = [...DocumentRoute];

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
        BadgeModule,
        VectorVisualizationComponent,
        ToastModule,
        TooltipModule,
        TabViewModule,
        ProgressBarModule
    ],
  declarations: [
    DocumentComponent,
    DocumentUpdateComponent,
    AbstractDocumentComponent],
  providers: [MessageService],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DocumentModule {
}
