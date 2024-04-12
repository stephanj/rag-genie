import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';
import {GenieSharedModule} from '../../shared/shared.module';
import {embeddingModelRoute} from './embedding-model.route';
import {EmbeddingModelComponent} from './embedding-model.component';
import {TableModule} from 'primeng/table';
import {ToastModule} from 'primeng/toast';
import {TooltipModule} from 'primeng/tooltip';
import { MessageService } from 'primeng/api';

const ENTITY_STATES = [...embeddingModelRoute];

@NgModule({
  imports: [
    GenieSharedModule,
    RouterModule.forChild(ENTITY_STATES),
    TableModule,
    ToastModule,
    TooltipModule
  ],
    declarations: [
        EmbeddingModelComponent
    ],
    providers: [MessageService],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class EmbeddingModelModule {
}
