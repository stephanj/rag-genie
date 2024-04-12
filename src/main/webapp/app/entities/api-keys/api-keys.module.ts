import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {GenieSharedModule} from '../../shared/shared.module';
import {apiKeysRoute} from './api-keys.route';
import {ApiKeysComponent} from './api-keys.component';
import {TableModule} from 'primeng/table';
import {ToastModule} from 'primeng/toast';
import {ProgressBarModule} from 'primeng/progressbar';
import {ConfirmDialogModule} from 'primeng/confirmdialog';
import {InputTextModule} from 'primeng/inputtext';
import {ButtonModule} from 'primeng/button';
import { ContentListComponent } from '../../shared/content-list/content-list.component';
import {DialogModule} from 'primeng/dialog';
import {DropdownModule} from 'primeng/dropdown';
import {TooltipModule} from 'primeng/tooltip';
import { MessageService } from 'primeng/api';

const ENTITY_STATES = [...apiKeysRoute];

@NgModule({
    imports: [
        GenieSharedModule,
        RouterModule.forChild(ENTITY_STATES),
        InputTextModule,
        ToastModule,
        InputTextModule,
        ButtonModule,
        ProgressBarModule,
        ConfirmDialogModule,
        ContentListComponent,
        TableModule,
        DialogModule,
        DropdownModule,
        TooltipModule
    ],
    declarations: [
        ApiKeysComponent
    ],
    providers: [MessageService],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ApiKeysModule {
}
