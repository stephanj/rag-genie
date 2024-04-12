import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {GenieVectorVisualizationRoute} from './vector-visualization.route';

const ENTITY_STATES = [...GenieVectorVisualizationRoute];

@NgModule({
  imports: [RouterModule.forChild(ENTITY_STATES)],
  exports: [RouterModule],
})
export class VectorVisualizationModule {
}
