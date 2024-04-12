import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import { PanelModule } from 'primeng/panel';
import {Button, ButtonModule} from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { PaginatorModule } from 'primeng/paginator';
import { ProgressBarModule } from 'primeng/progressbar';
import { FormBuilder, ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmationService, MessageService } from 'primeng/api';
import { LanguageModelDropdownComponent} from '../../../shared/language-model-dropdown/language-model-dropdown.component';
import { InputTextModule} from 'primeng/inputtext';
import { EmbeddingModelService} from '../../../entities/embedding-model/embedding-model.service';
import { ModelSelectItem} from '../../../shared/model/model-select-item.model';
import { IEmbeddingModel} from '../../../shared/model/embedding-model.model';
import { ApiKeysService} from '../../../entities/api-keys/api-keys.service';
import { Subscription} from 'rxjs';
import { AgentResearcherService} from './agent-researcher.service';

@Component({
  selector: 'genie-refinement',
  standalone: true,
  imports: [
    PanelModule,
    ButtonModule,
    ConfirmDialogModule,
    ToastModule,
    AutoCompleteModule,
    PaginatorModule,
    ProgressBarModule,
    ReactiveFormsModule,
    TooltipModule,
    LanguageModelDropdownComponent,
    InputTextModule,
  ],
  templateUrl: './agent-researcher.component.html'
})
export class AgentResearcherComponent implements OnInit, OnDestroy {

  selectedLanguageModel: ModelSelectItem | null = null;
  embeddingModels: IEmbeddingModel[] = [];
  researching = false;
  researchForm: UntypedFormGroup;

  @ViewChild("btn") researchButton!: Button;

  private subscription: Subscription = new Subscription();

  constructor(protected embeddingModelService: EmbeddingModelService,
              protected apiKeyService: ApiKeysService,
              protected agentResearchService: AgentResearcherService,
              protected formBuilder: FormBuilder,
              protected messageService: MessageService,
              protected confirmationService: ConfirmationService,
              protected fb: UntypedFormBuilder) {
    this.researchForm = this.initForm();
  }

  ngOnInit(): void {
    this.getEmbeddingModels();
    // TODO Check if SerpAPI is available
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private initForm(): UntypedFormGroup {
    return this.fb.group({
      question: ['', Validators.required]
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

  modelSelected($event: any): void {

    this.selectedLanguageModel = $event;

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

  startResearch(): void {
    this.researching = true;
    this.researchForm.disable();
    this.researchButton.label = "Researching";
    const data = {
      languageModelDTO: this.selectedLanguageModel,
      question: this.researchForm.get('question')?.value
    }
    this.agentResearchService.startResearch(data)
      .subscribe({
          next: value => console.log('result:', value)
      });
  }

  visit(url: string) {
    window.open(url, '_blank');
  }
}
