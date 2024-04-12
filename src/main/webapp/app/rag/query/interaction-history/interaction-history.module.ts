import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { GenieInteractionRoute } from './interaction-history.route';
import { MessageService } from 'primeng/api';

const ENTITY_STATES = [...GenieInteractionRoute];

@NgModule({
  imports: [RouterModule.forChild(ENTITY_STATES)],
  exports: [RouterModule],
  providers: [MessageService]
})
export class InteractionHistoryModule {
}
