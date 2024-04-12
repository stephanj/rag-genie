import {Component, OnDestroy, OnInit} from '@angular/core';
import { LanguageModelService } from '../../../entities/language-model/language-model.service';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { map, takeUntil } from 'rxjs/operators';
import { DocumentService } from '../../basic/document/document.service';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Observable, Subject } from 'rxjs';
import { SummaryService } from './summary.service';
import { ResponseSummary } from '../chain-of-density/chain-of-density.component';
import {ModelSelectItem} from '../../../shared/model/model-select-item.model';
import {IContent} from '../../../shared/model/content.model';
import {EmbeddingModel, IEmbeddingModel} from '../../../shared/model/embedding-model.model';
import {EmbeddingModelService} from '../../../entities/embedding-model/embedding-model.service';

interface BasicSummaryResponse {
  summaries: string[];
}

interface CodSummaryResponse {
  summaries: ResponseSummary[];
}

@Component({
  standalone: true,
  template: ''
})
export class BaseSummaryComponent implements OnInit, OnDestroy {

  modelForm!: FormGroup;

  summaries: any[] = [];
  summarizing = false;
  languageModelId: number = -1;

  chunks: string[] = [];

  embeddingModels: IEmbeddingModel[] = [];
  embeddingModel!: EmbeddingModel;

  saving = false;
  summaryDuration = 0;

  selectedContent: IContent | undefined;

  protected readonly onDestroy = new Subject<void>();

  constructor(protected formBuilder: FormBuilder,
              protected languageModelService: LanguageModelService,
              protected vectorDocumentService: DocumentService,
              protected summaryService: SummaryService,
              protected embeddingService: EmbeddingModelService,
              protected confirmationService: ConfirmationService,
              protected messageService: MessageService) {
    this.modelForm = this.formBuilder.group({
      temperature: new FormControl(0.7, [Validators.min(0), Validators.max(2)]),
    });
  }

  ngOnInit(): void {
    this.getAvailableEmbeddingModels();
  }

  ngOnDestroy(): void {
    this.onDestroy.next();
    this.onDestroy.complete();
  }

  onContentSelected(content: IContent): void {
    this.selectedContent = content;
  }

  getAvailableEmbeddingModels(): void {
    this.embeddingService.query()
      .subscribe((res) => {
        if (res.body) {
          this.embeddingModels = res.body;
        }
      });
  }

  protected selectedLanguageModel(modelSelectItem: ModelSelectItem | null): void {
    if (modelSelectItem && modelSelectItem.id) {
      this.languageModelId = modelSelectItem.id;
    }
  }

  summarize(method: 'cod' | 'basic'): void {
    this.summarizing = true;

    if (this.languageModelId) {
      const temperature = this.modelForm.get('temperature')?.value;
      const startTime = Date.now();

      let summarizationObservable: Observable<BasicSummaryResponse | CodSummaryResponse>;
      if (method === 'cod') {
        summarizationObservable = this.summaryService.codSummarization(this.languageModelId, temperature, this.chunks)
          .pipe(map(res => ({summaries: res})));
      } else {
        summarizationObservable = this.summaryService.basicSummarization(this.languageModelId, temperature, this.chunks)
          .pipe(map(res => ({summaries: res})));
      }

      summarizationObservable.subscribe((res: BasicSummaryResponse | CodSummaryResponse) => {
        if ('summaries' in res) {
          this.summaries = res.summaries; // Adjust this line based on how you want to handle the summaries
        }
        this.summaryDuration = Date.now() - startTime;
        this.summarizing = false;
      });
    }
  }

  protected storeChunks(contentId: number, chunks: string[]): void {
    this.saving = true;
    if (this.embeddingModel.id === undefined) {
        this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'No embedding model selected'
        });
        return;
    }

    this.vectorDocumentService.storeChunksWithRef(contentId, chunks, this.embeddingModel.id)
      .pipe(takeUntil(this.onDestroy))
      .subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: 'Documents imported in vector database'
          });
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to import documents in vector database'
          });
        }
      });
  }

  protected getSummaryDuration(): string {
    return this.summaryDuration < 1000 ? this.summaryDuration + ' ms' : (this.summaryDuration / 1000) + ' s';
  }

  getTemperature(): string {
    return this.modelForm.get(['temperature'])!.value;
  }
}
