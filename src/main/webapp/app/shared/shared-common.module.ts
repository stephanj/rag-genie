import { NgModule } from '@angular/core';
import { GenieSharedLibsModule } from './shared-libs.module';

@NgModule({
  imports: [GenieSharedLibsModule],
  exports: [GenieSharedLibsModule],
})
export class GenieSharedCommonModule {}
