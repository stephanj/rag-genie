import { Component } from '@angular/core';
import { ContentListComponent } from '../../../shared/content-list/content-list.component';
import { PanelModule } from 'primeng/panel';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { PaginatorModule } from 'primeng/paginator';
import { ProgressBarModule } from 'primeng/progressbar';
import { GenieTextSplitterModule } from '../../basic/text-splitter/text-splitter.module';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { LanguageModelService } from '../../../entities/language-model/language-model.service';
import { SummaryService } from '../summary/summary.service';
import { DocumentService } from '../../basic/document/document.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { BaseSummaryComponent } from '../summary/base-summary.component';
import {SliderModule} from 'primeng/slider';
import {
    LanguageModelDropdownComponent
} from '../../../shared/language-model-dropdown/language-model-dropdown.component';
import {EmbeddingModelService} from '../../../entities/embedding-model/embedding-model.service';

export class ResponseSummary {
  missingEntities?: string;
  denserSummary?: string;
}

@Component({
  selector: 'genie-refinement',
  standalone: true,
    imports: [
        ContentListComponent,
        PanelModule,
        ButtonModule,
        ConfirmDialogModule,
        ToastModule,
        AutoCompleteModule,
        PaginatorModule,
        ProgressBarModule,
        GenieTextSplitterModule,
        ReactiveFormsModule,
        TableModule,
        TooltipModule,
        SliderModule,
        LanguageModelDropdownComponent
    ],
  templateUrl: './chain-of-density.component.html',
  styleUrl: './chain-of-density.component.scss'
})
export class ChainOfDensityComponent extends BaseSummaryComponent {

  chunks: string[] = [];
  summaries: ResponseSummary[] = [];
  summarizing = false;

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
    this.summarize('cod');
  }

  onAvailableChunks(chunks: string[]): void {
    this.chunks = chunks;
  }

  storeSummariesInDB() : void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to import summaries in vector database?',
      accept: () => {
        this.storeChunks();
      }
    });
  }

  protected storeChunks(): void {
    const chunks = this.summaries.map(summary => "Missing:" + summary.missingEntities + "\nSummary:" + summary.denserSummary);
    if (this.selectedContent?.id && chunks.length > 0) {
      // Convert ResponseSummary using field denserSummary to string[]
      const summaries: string[] = [];
      this.summaries.forEach(summary => {
        if (summary.denserSummary) {
          summaries.push(summary.denserSummary);
        }
      });
      super.storeChunks(this.selectedContent.id, summaries);
    } else {
      this.messageService.add({severity: 'error', summary: 'Error', detail: 'No content selected'});
    }
  }
}
