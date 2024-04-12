import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ImportRoute } from './import.route';
import { MessageService } from 'primeng/api';

const ENTITY_STATES = [...ImportRoute];

@NgModule({
  imports: [RouterModule.forChild(ENTITY_STATES)],
  exports: [RouterModule],
  providers: [MessageService]
})
export class ImportModule {
}
