import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { RefinementRoute } from './summary.route';

const ENTITY_STATES = [...RefinementRoute];
@NgModule({
  imports: [RouterModule.forChild(ENTITY_STATES)],
  exports: [RouterModule],
})
export class SummaryModule {
}
