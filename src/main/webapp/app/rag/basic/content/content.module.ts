import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { GenieContentRoute } from './content.route';

const ENTITY_STATES = [...GenieContentRoute];

@NgModule({
  imports: [RouterModule.forChild(ENTITY_STATES)],
  exports: [RouterModule],
})
export class ContentModule {
}
