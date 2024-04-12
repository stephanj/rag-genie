import { Component } from '@angular/core';
import { ContentListComponent } from '../../../shared/content-list/content-list.component';
import { PanelModule } from 'primeng/panel';
import { ButtonModule } from 'primeng/button';
import { NgForOf } from '@angular/common';
import { GenieTextSplitterModule } from '../../basic/text-splitter/text-splitter.module';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GenieSharedModule } from '../../../shared/shared.module';
import { SummaryService } from './summary.service';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { LanguageModelService } from '../../../entities/language-model/language-model.service';
import { TableModule } from 'primeng/table';
import { InputNumberModule } from 'primeng/inputnumber';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DocumentService } from '../../basic/document/document.service';
import { ProgressBarModule } from 'primeng/progressbar';
import { BaseSummaryComponent } from './base-summary.component';
import {SliderModule} from 'primeng/slider';
import {
  LanguageModelDropdownComponent
} from '../../../shared/language-model-dropdown/language-model-dropdown.component';
import {IContent} from '../../../shared/model/content.model';
import {DropdownModule} from 'primeng/dropdown';
import {EmbeddingModelService} from '../../../entities/embedding-model/embedding-model.service';

@Component({
  selector: 'genie-refinement',
  standalone: true,
  imports: [
    ContentListComponent,
    PanelModule,
    ButtonModule,
    NgForOf,
    GenieTextSplitterModule,
    FormsModule,
    GenieSharedModule,
    ReactiveFormsModule,
    AutoCompleteModule,
    TableModule,
    InputNumberModule,
    TooltipModule,
    ConfirmDialogModule,
    ProgressBarModule,
    SliderModule,
    LanguageModelDropdownComponent,
    DropdownModule
  ],
  templateUrl: './summary.component.html',
  styleUrl: './summary.component.scss'
})
export class SummaryComponent extends BaseSummaryComponent {

  summaries: string[] = [];
  chunks: string[] = [];

  constructor(protected formBuilder: FormBuilder,
              protected languageModelService: LanguageModelService,
              protected summaryService: SummaryService,
              protected vectorDocumentService: DocumentService,
              protected embeddingService: EmbeddingModelService,
              protected messageService: MessageService,
              protected confirmationService: ConfirmationService) {
    super(formBuilder, languageModelService, vectorDocumentService, summaryService, embeddingService, confirmationService, messageService);
  }

  onSummarizeClick(): void {
    this.modelForm.disable();
    this.summarize('basic');
  }

  onAvailableChunks(chunks: string[]): void {
    this.chunks = chunks;
  }

  storeSummariesInDB() : void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to save the summaries in the vector database?',
      accept: () => {
        if (this.selectedContent?.id && this.chunks.length > 0) {
          super.storeChunks(this.selectedContent.id, this.summaries);
        } else {
          this.messageService.add({severity: 'error', summary: 'Error', detail: 'No content selected'});
        }
      }
    });
  }
}
