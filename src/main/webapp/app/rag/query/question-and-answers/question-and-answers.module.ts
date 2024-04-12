import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { QuestionAndAnswerRoute } from './question-and-answers.route';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { ButtonModule } from 'primeng/button';
import { GenieSharedModule } from '../../../shared/shared.module';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { MessageService } from 'primeng/api';
import {ClipboardModule} from '@angular/cdk/clipboard';

const ENTITY_STATES = [...QuestionAndAnswerRoute];

@NgModule({
  imports: [
    AutoCompleteModule,
    ButtonModule,
    GenieSharedModule,
    DropdownModule,
    InputTextModule,
    InputTextareaModule,
    ClipboardModule,
    RouterModule.forChild(ENTITY_STATES)
  ],
  providers: [MessageService],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class QuestionAndAnswersModule {
}
