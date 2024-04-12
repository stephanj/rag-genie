import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {evaluationResultsRoute} from './evaluation-results.route';
import {EvaluationResultsComponent} from './evaluation-results.component';
import {EvaluationResultsViewComponent} from './evaluation-results-view.component';
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
import { TooltipModule } from 'primeng/tooltip';
import {ChipModule} from 'primeng/chip';
import {ChartModule} from 'primeng/chart';
import { MessageService } from 'primeng/api';

const ENTITY_STATES = [...evaluationResultsRoute];

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
        TooltipModule,
        ChipModule,
        ChartModule
    ],
    declarations: [
        EvaluationResultsComponent,
        EvaluationResultsViewComponent
    ],
    providers: [MessageService],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class EvaluationResultsModule {
}
