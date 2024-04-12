import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {GenieSharedModule} from '../../shared/shared.module';
import {languageModelRoute} from './language-model.route';
import {LanguageModelComponent} from './language-model.component';
import {LanguageModelUpdateComponent} from './language-model-update.component';
import {TableModule} from 'primeng/table';
import {ToastModule} from 'primeng/toast';
import {ProgressBarModule} from 'primeng/progressbar';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {InputTextModule} from 'primeng/inputtext';
import {ButtonModule} from 'primeng/button';
import {DropdownModule} from 'primeng/dropdown';
import {InputNumberModule} from 'primeng/inputnumber';
import { ContentListComponent } from '../../shared/content-list/content-list.component';
import { CheckboxModule } from 'primeng/checkbox';
import {TooltipModule} from 'primeng/tooltip';
import {ChartModule} from 'primeng/chart';
import { RadioButtonModule } from 'primeng/radiobutton';
import { SelectButtonModule } from 'primeng/selectbutton';
import {InputTextareaModule} from 'primeng/inputtextarea';
import { MessageService } from 'primeng/api';

const ENTITY_STATES = [...languageModelRoute];

@NgModule({
    imports: [
        GenieSharedModule,
        RouterModule.forChild(ENTITY_STATES),
        InputTextModule,
        TableModule,
        ToastModule,
        InputTextModule,
        ButtonModule,
        ProgressBarModule,
        ConfirmDialogModule,
        DropdownModule,
        InputNumberModule,
        ContentListComponent,
        CheckboxModule,
        TooltipModule,
        ChartModule,
        RadioButtonModule,
        SelectButtonModule,
        InputTextareaModule
    ],
    declarations: [
        LanguageModelComponent,
        LanguageModelUpdateComponent
    ],
    providers: [MessageService],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class LanguageModelModule {
}
