import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {evaluationRoute} from './evaluation.route';
import {EvaluationComponent} from './evaluation.component';
import {EvaluationUpdateComponent} from './evaluation-update.component';
import {TableModule} from 'primeng/table';
import {ToastModule} from 'primeng/toast';
import {ProgressBarModule} from 'primeng/progressbar';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {InputTextModule} from 'primeng/inputtext';
import {BadgeModule} from 'primeng/badge';
import {ButtonModule} from 'primeng/button';
import {DropdownModule} from 'primeng/dropdown';
import {InputNumberModule} from 'primeng/inputnumber';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { ChipsModule } from 'primeng/chips';
import { DialogModule } from 'primeng/dialog';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { GenieSharedModule } from '../../../shared/shared.module';
import { SliderModule } from 'primeng/slider';
import {
  LanguageModelDropdownComponent
} from '../../../shared/language-model-dropdown/language-model-dropdown.component';
import { MessageService } from 'primeng/api';

const ENTITY_STATES = [...evaluationRoute];

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
    BadgeModule,
    DropdownModule,
    InputNumberModule,
    CheckboxModule,
    InputTextareaModule,
    ChipsModule,
    DialogModule,
    AutoCompleteModule,
    SliderModule,
    LanguageModelDropdownComponent
  ],
    declarations: [
        EvaluationComponent,
        EvaluationUpdateComponent
    ],
    providers: [MessageService],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class EvaluationModule {
}
