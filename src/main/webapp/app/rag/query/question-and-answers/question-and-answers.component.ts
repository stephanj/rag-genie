import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { ConfirmationService, MessageService, SharedModule } from 'primeng/api';
import { TableModule } from 'primeng/table';
import { GenieSharedCommonModule } from '../../../shared/shared-common.module';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ContentListComponent } from '../../../shared/content-list/content-list.component';
import { InputNumberModule } from 'primeng/inputnumber';
import { SplitterFormComponent } from '../../../shared/splitter-form/splitter-form.component';
import { PanelModule } from 'primeng/panel';
import { DocumentResult } from '../../../shared/model/document-result.model';
import { QuestionResponse } from '../../../shared/model/question-response.model';
import { MessagesModule } from 'primeng/messages';
import { ProgressBarModule } from 'primeng/progressbar';
import { catchError } from 'rxjs/operators';
import { EMPTY, Observable, Subject, Subscription, throwError} from 'rxjs';
import { ToastModule } from 'primeng/toast';
import { DocumentService } from '../../basic/document/document.service';
import { GenieSharedModule } from '../../../shared/shared.module';
import { QuestionAndAnswersService } from './question-and-answers.service';
import { TooltipModule } from 'primeng/tooltip';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { ModelSelectItem } from '../../../shared/model/model-select-item.model';
import { SliderModule} from 'primeng/slider';
import { RouterLink} from '@angular/router';
import { CheckboxModule} from 'primeng/checkbox';
import { LocalStorageService } from 'ngx-webstorage';
import { ApiKeysService } from '../../../entities/api-keys/api-keys.service';
import { HttpParams } from '@angular/common/http';
import { LanguageModelDropdownComponent } from '../../../shared/language-model-dropdown/language-model-dropdown.component';
import { DropdownModule } from 'primeng/dropdown';
import { ListboxModule } from 'primeng/listbox';
import {IEmbeddingModel} from '../../../shared/model/embedding-model.model';
import {EmbeddingModelService} from '../../../entities/embedding-model/embedding-model.service';
import { ContentService, TokenInfo } from '../../basic/content/content.service';

@Component({
  selector: 'genie-question-and-answers',
  standalone: true,
  imports: [
    ButtonModule,
    SharedModule,
    TableModule,
    GenieSharedCommonModule,
    InputTextModule,
    InputTextareaModule,
    ReactiveFormsModule,
    ContentListComponent,
    InputNumberModule,
    PanelModule,
    MessagesModule,
    ProgressBarModule,
    ToastModule,
    TooltipModule,
    GenieSharedModule,
    AutoCompleteModule,
    SliderModule,
    RouterLink,
    CheckboxModule,
    LanguageModelDropdownComponent,
    DropdownModule,
    ListboxModule
  ],
  templateUrl: './question-and-answers.component.html',
  styleUrl: './question-and-answers.component.scss'
})
export class QuestionAndAnswersComponent implements OnInit, OnDestroy {

  @ViewChild('documentList') documentListComponent!: ContentListComponent;
  @ViewChild('splitter') splitter!: SplitterFormComponent;

  private readonly onDestroy = new Subject<void>();

  private subscription: Subscription = new Subscription();

  questionResponse!: QuestionResponse;
  model = '';
  usedDocuments: DocumentResult[] = [];

  chunks: DocumentResult[] = [];
  similarityPanelClosed = true;
  answerPanelClosed = true;

  queryForm!: FormGroup;

  askingQuestion = false;

  selectedLanguageModel: ModelSelectItem | null = null;
  embeddingModels: IEmbeddingModel[] = [];

  similarityDuration = 0;
  questionDuration = 0;
  useAllDocs = false;
  formatAnswer = false;
  reRank = false;

  systemPrompt = '';
  questionPrompt = '';

  totalDocuments = 0;

  constructor(private apiKeyService: ApiKeysService,
              private questionAndAnswersService: QuestionAndAnswersService,
              private vectorDocumentService: DocumentService,
              private contentService: ContentService,
              private embeddingModelService: EmbeddingModelService,
              private formBuilder: FormBuilder,
              protected localStorageService: LocalStorageService,
              protected messageService: MessageService,
              protected confirmationService: ConfirmationService) {
    this.createQueryForm();
  }

  private createQueryForm() {
    this.queryForm = this.formBuilder.group({
      prompt: new FormControl('', Validators.required),
      question: new FormControl('', Validators.required),
      allDocs: new FormControl(false, Validators.required),
      maxResults: new FormControl(5, Validators.required),
      minScore: new FormControl(70, Validators.required),
      temperature: new FormControl(0.7, Validators.required),
      topP: new FormControl(0.5, Validators.required),
      maxOutputTokens: new FormControl(4000, Validators.required),
      searchWeb: new FormControl(false),
      formatAnswer: new FormControl(false),
      rerankAnswers: new FormControl(false),
      timeout: new FormControl(180, Validators.required),
      embeddingModel: new FormControl('', Validators.required)
    });
  }

  ngOnInit(): void {
    this.checkIfDocumentsExist();
    this.getEmbeddingModels();
    this.getStoredPromptInfo();
  }

  ngOnDestroy(): void {
    this.onDestroy.next();
    this.onDestroy.complete();
    this.subscription.unsubscribe();
  }

  getStoredPromptInfo(): void {
    this.systemPrompt = this.localStorageService.retrieve('systemPrompt');
    this.questionPrompt = this.localStorageService.retrieve('questionPrompt');

    this.queryForm.patchValue({
      prompt: this.systemPrompt,
      question: this.questionPrompt
    });
  }

  getEmbeddingModels(): void {
    this.embeddingModelService.usedModels()
      .subscribe((res) => {
        if (res.body) {
          this.embeddingModels = res.body;
        }
    });
  }

  checkIfDocumentsExist() {
    this.contentService.getDocumentsCount().subscribe(
      (res) => {
        this.totalDocuments = Number(res.headers.get('X-Total-Count') || '0');
        if (this.totalDocuments === 0) {
          this.queryForm.patchValue({allDocs: true});
          this.messageService.add({
            severity: 'warn',
            summary: 'No documents',
            sticky: true,
            detail: 'There are no documents available for use.  As a result, only all content option is available.'});
        }
      });
  }

  processQuestion(): void {
    this.queryForm.disable();

    this.questionResponse = new QuestionResponse();
    this.usedDocuments = [];
    this.askingQuestion = true;

    this.localStorageService.store('systemPrompt', this.queryForm.get(['prompt'])!.value);
    this.localStorageService.store('questionPrompt', this.queryForm.get(['question'])!.value);

    const startTime = Date.now();

    const questionData = this.getQuestionData();

    this.askQuestion(questionData)
      .pipe(
        catchError((error) => {
          // Handle any error that occurs during the entire chain
          this.askingQuestion = false;
          this.queryForm.enable();
          this.messageService.add({severity: 'error', summary: 'Error', detail: error.message});
          return EMPTY; // Use EMPTY or throwError as per your error handling logic
        })
      )
      .subscribe({
        next: (res: QuestionResponse) => {
          this.questionDuration = Date.now() - startTime - this.similarityDuration;
          this.askingQuestion = false;
          this.answerPanelClosed = false;
          this.questionResponse = res;

          this.queryForm.enable();

          if (res.usedDocuments) {
            this.usedDocuments = res.usedDocuments;
            this.similarityPanelClosed = false;
          }
        }
      });
  }

  languageModelSelected($event: any): void {

    this.selectedLanguageModel = $event;

    if ($event != null && $event.contextWindow && $event.contextWindow > 0) {
      this.useAllDocs = true;
    }

    if ($event != null && $event.apiKeyRequired) {
      this.subscription.add(this.apiKeyService
        .checkApiKey($event.type)
        .subscribe({
          next: () => {
            // Handle successful API key check if necessary
          },
          error: () => {
            this.messageService.add({
              severity: 'warn',
              sticky: true,
              summary: 'API Key required',
              detail: 'This model requires an API key to use. Please add your key via the left bottom menu: API Keys.'
            });
          }
        })
      );
    }
  }

  private getQuestionData() {
    return {
      prompt: this.queryForm.get(['prompt'])!.value,
      modelId: this.selectedLanguageModel?.id,
      question: this.queryForm.get(['question'])!.value,
      allDocs: this.queryForm.get(['allDocs'])!.value,
      formatResponse: this.queryForm.get(['formatAnswer'])!.value,
      temperature: this.queryForm.get(['temperature'])!.value,
      maxOutputTokens: this.queryForm.get(['maxOutputTokens'])!.value,
      topP: this.queryForm.get(['topP'])!.value,
      maxResults: this.queryForm.get(['maxResults'])!.value,
      minScore: (this.queryForm.get(['minScore'])!.value) / 100,
      searchWeb: this.queryForm.get(['searchWeb'])!.value,
      rerank: this.queryForm.get(['rerankAnswers'])!.value,
      timeout: this.queryForm.get(['timeout'])!.value,
      embeddingModelRefId: this.queryForm.get(['embeddingModel'])!.value.id,
      languageModelDTO: this.selectedLanguageModel
    };
  }

  private askQuestion(data: any): Observable<QuestionResponse> {
    return this.questionAndAnswersService.askQuestion(data)
      .pipe(catchError(error => {
          this.messageService.add({severity: 'error', summary: 'Question Error', detail: error.message, sticky: true});
          return throwError(() => new Error(error.message));
        })
    );
  }

  deleteDocument(documentId: string): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete this vector item?',
      accept: () => {
        if (documentId !== undefined) {
          this.vectorDocumentService.delete(documentId).subscribe(() => {
            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Document removed successfully' });
          });
        }
      }
    });
  }

  getAnswerDuration(): string {
    return this.questionDuration < 1000 ? this.questionDuration + ' ms' : (this.questionDuration / 1000) + ' s';
  }

  getTemperature(): string {
    return this.queryForm.get(['temperature'])!.value;
  }

  allDocsChanged($event: any): void {
    this.useAllDocs = $event.checked;
  }

  formatResponse($event: any): void {
    this.formatAnswer = $event.checked;
  }

  rerankResponse($event: any): void {
    this.reRank = $event.checked;
  }

  calcCost() {
    if (this.selectedLanguageModel && this.selectedLanguageModel.id) {

      // TODO Include EmbeddingModel cost
      const param = new HttpParams()
        .set('maxOutputTokens', this.queryForm.get('maxOutputTokens')?.value)
        .set('modelId', this.selectedLanguageModel?.id);

      this.contentService.getAnalysisCost(param).subscribe({
        next: (res) => {
          if (res.body) {
            const tokenInfo: TokenInfo = res.body;
            const inputMsg = 'For ' + tokenInfo?.inputTokens + ' input tokens, the cost is: ' + tokenInfo?.inputCost.toFixed(4) + '$';
            const outputMsg = 'For ' + tokenInfo?.outputTokens + ' output tokens, the cost is: ' + tokenInfo?.outputCost.toFixed(4) + '$';
            const totalMsg = 'Total cost using ' + this.selectedLanguageModel?.label +  ' would be ' + (tokenInfo?.inputCost + tokenInfo?.outputCost).toFixed(4) + '$';
            this.messageService.add({ severity: 'info', summary: 'Input Cost', detail: inputMsg, sticky: true });
            this.messageService.add({ severity: 'info', summary: 'Output Cost', detail: outputMsg, sticky: true });
            this.messageService.add({ severity: 'info', summary: 'Total Cost', detail: totalMsg, sticky: true });
          }
        },
        error: () => {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to calculate cost' });
        }
      });
    }
  }

  async copyAnswerToClipboard(text: string) {
    try {
      await navigator.clipboard.writeText(text);
      this.messageService.add({ severity: 'info', summary: 'Copied', detail: 'Answer copied to clipboard' });
    } catch (err) {
      this.messageService.add({ severity: 'error' , summary: 'Error', detail: 'Failed to copy answer to clipboard' });
    }
  }
}
