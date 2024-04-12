import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit} from '@angular/core';

import { ChatbotService } from './chatbot.service';
import { Subscription } from 'rxjs';
import { Account } from '../../../shared/model/account.model';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { ModelSelectItem } from '../../../shared/model/model-select-item.model';
import { IEmbeddingModel } from '../../../shared/model/embedding-model.model';
import { ApiKeysService } from '../../../entities/api-keys/api-keys.service';
import { MessageService } from 'primeng/api';
import { EmbeddingModelService} from '../../../entities/embedding-model/embedding-model.service';
import { FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

export class ChatMessage {
  constructor(public text: string,
              public createdAt: any,
              public ownerId: number) {}
}

@Component({
  selector: 'genie-chat-terminal',
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default
})
export class ChatBotComponent implements OnInit, OnDestroy {
  @Input() sidebarVisible = false;

  loading = false;
  textContent = '';
  account?: Account;

  private subscription: Subscription = new Subscription();
  chatForm!: FormGroup;

  private userId = -1;

  messages: ChatMessage[] = [];
  reRank = false;

  selectedLanguageModel: ModelSelectItem | null = null;
  embeddingModels: IEmbeddingModel[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private chatService: ChatbotService,
    private apiKeyService: ApiKeysService,
    private embeddingModelService: EmbeddingModelService,
    private messageService: MessageService,
    private sanitizer: DomSanitizer
  ) {
    this.createChatForm();
  }

  ngOnInit(): void {
    this.getEmbeddingModels();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  private createChatForm() {
    this.chatForm = this.formBuilder.group({
      question: new FormControl('', Validators.required),
      embeddingModel: new FormControl('', Validators.required),
      rerankAnswers: new FormControl(false),
      temperature: new FormControl(0.5, Validators.required),
      maxTokens: new FormControl(2000, Validators.required),
      timeout: new FormControl(60, Validators.required),
      topP: new FormControl(0.8, Validators.required),
      minScore: new FormControl(0.6, Validators.required),
      maxResults: new FormControl(10, Validators.required)
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

  parseTextToHtml(text: string): SafeHtml {
    const regex = /\[([^\]]+)\]\(([^)]+)\)/g;
    const replacedText = text.replace(regex, `<a href="$2" class="text-color-secondary">$1</a>`);
    return this.sanitizer.bypassSecurityTrustHtml(replacedText);
  }

  parseDate(timestamp: number) {
    return new Date(timestamp).toTimeString().split(':').slice(0, 2).join(':');
  }

  sendMessage(): void {
    this.chatForm.disable();
    this.loading = true;

    const question = this.chatForm.get('question')?.value;
    this.messages.push(new ChatMessage(question, new Date(), this.userId));

    const requestBody = {
      question: question,
      rerank: this.chatForm.get('rerankAnswers')?.value,
      embeddingModelRefId: this.chatForm.get('embeddingModel')?.value.id,
      languageModelDTO: this.selectedLanguageModel,
      temperature: this.chatForm.get('temperature')?.value,
      maxTokens: this.chatForm.get('maxTokens')?.value,
      timeout: this.chatForm.get('timeout')?.value,
      topP: this.chatForm.get('topP')?.value,
      minScore: this.chatForm.get('minScore')?.value,
      maxResults: this.chatForm.get('maxResults')?.value
    };

    this.chatService
      .chat(requestBody)
      .subscribe({
        next: (response) => {
          this.textContent = '';
          if (response) {
            this.messages.push(new ChatMessage(response, new Date(), -1));
          }
          this.loading = false;
          this.chatForm.enable();
        },
        error: (error) => {
          // Handle any errors that occur during the request
          console.error('Error occurred:', error);
          this.loading = false;
        },
        complete: () => {
          // This block is optional and can be used to execute any additional code
          // when the observable completes, which means it has emitted all its values.
          console.log('Request completed');
        }
      });

  }

  isLoading(): boolean {
    return this.loading;
  }

  getThemeImage(): string {
    return '/assets/images/chatbot/ragtest.png';
  }

  rerankResponse($event: any): void {
    this.reRank = $event.checked;
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

  getTemperature(): string {
    return this.chatForm.get(['temperature'])!.value;
  }

}
