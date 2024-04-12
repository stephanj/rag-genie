import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { NgbDateAdapter } from '@ng-bootstrap/ng-bootstrap';

import { NgbDateMomentAdapter } from './util/datepicker-adapter';
import { PanelModule } from 'primeng/panel';
import { TableModule } from 'primeng/table';
import { ToastModule } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { DropdownModule } from 'primeng/dropdown';
import { PaginatorModule } from 'primeng/paginator';
import { ProgressBarModule } from 'primeng/progressbar';
import { SplitButtonModule } from 'primeng/splitbutton';
import { TooltipModule } from 'primeng/tooltip';
import { FileUploadModule } from 'primeng/fileupload';
import { ConfirmationService } from 'primeng/api';
import { ReactiveFormsModule } from '@angular/forms';
import { NgJhipsterModule } from '@ng-jhipster/public_api';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule } from '@angular/router';
import { GenieSharedLibsModule } from './shared-libs.module';
import { GenieSharedCommonModule } from './shared-common.module';
import { GrabFocusDirective } from './util/grab-focus.directive';
import { ImageUploadComponent } from './image-upload/image-upload.component';
import { DialogModule } from 'primeng/dialog';
import { CheckboxModule } from 'primeng/checkbox';
import { TagModule } from 'primeng/tag';
import { AvatarGroupModule } from 'primeng/avatargroup';
import { AvatarModule } from 'primeng/avatar';
import { AutoCompleteModule } from 'primeng/autocomplete';

@NgModule({
  imports: [
    NgJhipsterModule,
    FontAwesomeModule,
    ReactiveFormsModule,
    ButtonModule,
    ConfirmDialogModule,
    GenieSharedLibsModule,
    GenieSharedCommonModule,
    InputTextareaModule,
    PanelModule,
    DropdownModule,
    PaginatorModule,
    ProgressBarModule,
    SplitButtonModule,
    TooltipModule,
    ToastModule,
    TableModule,
    FileUploadModule,
    RouterModule,
    DialogModule,
    CheckboxModule,
    TagModule,
    AvatarGroupModule,
    AvatarModule,
    AutoCompleteModule
  ],
  declarations: [
    ImageUploadComponent,
    GrabFocusDirective
  ],
  providers: [
    { provide: NgbDateAdapter, useClass: NgbDateMomentAdapter },
    ConfirmationService],
  exports: [
    GenieSharedCommonModule,
    ImageUploadComponent,
    GrabFocusDirective
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class GenieSharedModule {
  static forRoot(): any {
    return {
      ngModule: GenieSharedModule,
    };
  }
}
