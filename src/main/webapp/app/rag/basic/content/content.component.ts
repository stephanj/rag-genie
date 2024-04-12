import { Component, OnInit, ViewChild } from '@angular/core';
import { TableModule } from 'primeng/table';
import { FileUploadModule } from 'primeng/fileupload';
import { TabViewModule } from 'primeng/tabview';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { InputTextModule } from 'primeng/inputtext';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ToastModule } from 'primeng/toast';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DatePipe, DecimalPipe } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { RouterLink } from '@angular/router';
import { ContentListComponent } from '../../../shared/content-list/content-list.component';
import { CheckboxModule } from 'primeng/checkbox';
import { Content, ContentType } from '../../../shared/model/content.model';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { ModelSelectItem } from '../../../shared/model/model-select-item.model';
import { ApiKeysService } from '../../../entities/api-keys/api-keys.service';
import { ListboxModule } from 'primeng/listbox';
import { LanguageModelService } from '../../../entities/language-model/language-model.service';
import { HttpParams } from '@angular/common/http';
import { TooltipModule } from 'primeng/tooltip';
import { InputNumberModule } from 'primeng/inputnumber';
import {
  LanguageModelDropdownComponent
} from '../../../shared/language-model-dropdown/language-model-dropdown.component';
import { ContentService, TokenInfo } from './content.service';

@Component({
  selector: 'genie-import-content',
  standalone: true,
  imports: [
    TableModule,
    FileUploadModule,
    TabViewModule,
    InputTextareaModule,
    InputTextModule,
    FormsModule,
    ReactiveFormsModule,
    ToastModule,
    ConfirmDialogModule,
    DatePipe,
    DialogModule,
    ScrollPanelModule,
    RouterLink,
    ContentListComponent,
    CheckboxModule,
    DecimalPipe,
    AutoCompleteModule,
    ListboxModule,
    TooltipModule,
    InputNumberModule,
    LanguageModelDropdownComponent
  ],
  templateUrl: './content.component.html',
  styleUrl: './content.component.scss'
})
export class ContentComponent implements OnInit {

  @ViewChild(ContentListComponent, { static: true }) contentListComponent!: ContentListComponent;

  selectedContent: Content[] = [];
  totalTokensForUser = 0;

  recommendations = '';
  showDialog = false;
  showResultDialog = false;

  running = false;

  analyzeForm!: FormGroup;

  selectedModel: ModelSelectItem | null = null;

  auditRoles = [
    { label: 'Review code prompt', value: '0' },
    { label: 'Find bugs prompt', value: '1' },
    { label: 'Security review prompt', value: '2' },
    { label: 'Create unit tests prompt', value: '3' },
    { label: 'Create integration tests prompt', value: '4' }
  ];

  constructor(private formBuilder: FormBuilder,
              protected contentService: ContentService,
              protected languageModelService: LanguageModelService,
              protected apiKeyService: ApiKeysService,
              protected messageService: MessageService,
              protected confirmationService: ConfirmationService) {
    this.createAnalyzeForm();
  }

  private createAnalyzeForm() {
    this.analyzeForm = this.formBuilder.group({
      auditRole: [],
      customPrompt: [],
      includeComments: [],
      includeRelatedCode: [],
      maxOutputTokens: [null, Validators.required],
      model: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.getTotalUserTokens();
  }

  private getTotalUserTokens() {
    this.contentService.getTotalTokens().subscribe(total => {
      this.totalTokensForUser = total;
    });
  }

  onContentSelection(event: any) {
    this.selectedContent.push(event);
  }

  onContentUnselect(event: any) {
    this.selectedContent = this.selectedContent.filter(content => content.id !== event.id);
  }

  modelSelected($event: any): void {

    this.selectedModel = $event.value;

    if ($event.value.apiKeyRequired) {
      this.apiKeyService
        .checkApiKey($event.value.type)
        .subscribe(
          () => { },
          () => {
            this.messageService.add({
              severity: 'warn',
              sticky: true,
              summary: 'API Key required',
              detail: 'This model requires an API key to use.  Please add your key via the left bottom menu : API Keys.'});
          });
    }
  }

  analyzeSelectedContent() {
    if (this.selectedContent.length === 0) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Please select a file to analyze'
      });
      return;
    }

    if (this.selectedContent.length > 1) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Please select only one file to analyze'
      });
      return;
    }

    this.running = true;
    this.analyzeForm.disable();

    if (this.selectedContent[0]) {
      const selection = this.selectedContent[0];
      if (selection && selection.id && this.selectedModel && this.selectedModel.id) {

        const param = new HttpParams()
          .set('contentId', selection.id.toString())
          .set('modelId', this.selectedModel?.id)
          .set('promptId', this.analyzeForm.get('auditRole')?.value.value)
          .set('customPrompt', this.analyzeForm.get('customPrompt')?.value)
          .set('maxOutputTokens', this.analyzeForm.get('maxOutputTokens')?.value)
          .set('includeRelatedCode', this.analyzeForm.get('includeRelatedCode')?.value ? 'true' : 'false')
          .set('includeComments', this.analyzeForm.get('includeComments')?.value ? 'true' : 'false');

        this.contentService.analyzeContent(param)
          .subscribe({
            next: (res) => {
              if (res.body) {
                this.analyzeForm.enable();
                this.recommendations = res.body;
                this.showDialog = false;
                this.showResultDialog = true;
                this.running = false;
              }
            },
            error: (error) => {
              this.analyzeForm.enable();
              this.running = false;
              this.messageService.add({
                severity: 'error',
                summary: 'Error',
                detail: 'Failed to start content analysis'
              });
              console.error('Analysis error: ', error);
            }
          });
      }
    }
  }

  deleteAllContent() {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete all content?',
      accept: () => {
        this.contentService.deleteAll().subscribe({
          next: () => {
            this.totalTokensForUser = 0;
            this.contentListComponent.loadAll(); // Reload the list
            this.selectedContent = []; // Clear the selection
            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'All content deleted' });
          },
          error: () => {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete all content' });
          }
        });
      }
    });
  }

  deleteSelectedContent() {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete the selected content?',
      accept: () => {
        // Use a counter to track the number of successful deletions
        let deletionsCount = 0;

        this.selectedContent.forEach(content => {
          if (content && content.id) {
            this.contentService.deleteById(content.id).subscribe({
              next: () => {
                deletionsCount++;
                if (deletionsCount === this.selectedContent.length) {
                  this.updateTotalTokens(); // Update the total tokens after all deletions
                  this.contentListComponent.loadAll(); // Reload the list
                  this.selectedContent = []; // Clear the selection
                  this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Content deleted' });
                }
              },
              error: (error) => {
                this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete some content' });
                console.error('Deletion error: ', error);
              }
            });
          }
        });
      }
    });
  }

  updateTotalTokens() {
    // Call the server to get the most up-to-date token count
    this.contentService.getTotalTokens().subscribe(total => {
      this.totalTokensForUser = total;
    });
  }

  showAnalysisDialog() {
    this.showDialog = true;
  }

  calcCost() {
      if (this.selectedContent[0]) {
        const selection = this.selectedContent[0];

        if (selection && selection.id && this.selectedModel && this.selectedModel.id) {

          const param = new HttpParams()
            .set('contentId', selection.id.toString())
            .set('maxOutputTokens', this.analyzeForm.get('maxOutputTokens')?.value)
            .set('excludeRelatedCode', this.analyzeForm.get('excludeRelatedCode')?.value ? 'true' : 'false')
            .set('modelId', this.selectedModel?.id);

          this.contentService.getAnalysisCost(param).subscribe({
            next: (res) => {
              if (res.body) {
                const tokenInfo: TokenInfo = res.body;
                const inputMsg = 'For ' + tokenInfo?.inputTokens + ' input tokens, the cost is: ' + tokenInfo?.inputCost.toFixed(4) + '$';
                const outputMsg = 'For ' + tokenInfo?.outputTokens + ' output tokens, the cost is: ' + tokenInfo?.outputCost.toFixed(4) + '$';
                const totalMsg = 'Total cost using ' + this.selectedModel?.label +  ' would be ' + (tokenInfo?.inputCost + tokenInfo?.outputCost).toFixed(4) + '$';
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
  }

  disableWhenContentIsNotCode(selectedContent: Content): boolean {
    return (selectedContent === undefined || selectedContent.contentType !== ContentType.CODE);
  }
}
