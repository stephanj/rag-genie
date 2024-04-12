import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AgentResearcherRoute } from './agent-researcher.route';
import { MessageService } from 'primeng/api';

const ENTITY_STATES = [...AgentResearcherRoute];
@NgModule({
  imports: [RouterModule.forChild(ENTITY_STATES)],
  exports: [RouterModule],
  providers: [MessageService]
})
export class AgentResearcherModule {
}
