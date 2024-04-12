import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { ProgressBarModule } from 'primeng/progressbar';
import { GenieSharedModule } from '../../../shared/shared.module';
import { ChatBotRoute } from './chatbot.route';
import { ChatBotComponent } from './chatbot.component';
import { InputTextModule } from 'primeng/inputtext';
import { ContentListComponent } from '../../../shared/content-list/content-list.component';
import { PanelModule } from 'primeng/panel';
import {CheckboxModule} from 'primeng/checkbox';
import {DropdownModule} from 'primeng/dropdown';
import {
    LanguageModelDropdownComponent
} from '../../../shared/language-model-dropdown/language-model-dropdown.component';
import {TooltipModule} from 'primeng/tooltip';
import {InputNumberModule} from 'primeng/inputnumber';
import {SliderModule} from 'primeng/slider';
import { MessageService } from 'primeng/api';

const ENTITY_STATES = [...ChatBotRoute];

@NgModule({
    imports: [
        ButtonModule,
        GenieSharedModule,
        ProgressBarModule,
        RouterModule.forChild(ENTITY_STATES),
        InputTextModule,
        ContentListComponent,
        PanelModule,
        CheckboxModule,
        DropdownModule,
        LanguageModelDropdownComponent,
        TooltipModule,
        InputNumberModule,
        SliderModule
    ],
    declarations: [ChatBotComponent],
    providers: [MessageService],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GenieChatBotModule {
}
