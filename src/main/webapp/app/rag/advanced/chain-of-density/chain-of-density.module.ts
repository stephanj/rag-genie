import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { RefinementRoute } from './chain-of-density.route';

const ENTITY_STATES = [...RefinementRoute];
@NgModule({
  imports: [RouterModule.forChild(ENTITY_STATES)],
  exports: [RouterModule],
})
export class ChainOfDensityModule {
}
