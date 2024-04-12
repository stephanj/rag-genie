import { Component, OnDestroy, OnInit} from '@angular/core';
import { ConfirmationService, MessageService, SelectItem} from 'primeng/api';
import { ApiKeysService } from './api-keys.service';
import { Subscription} from 'rxjs';

@Component({
  selector: 'genie-api-keys',
  templateUrl: './api-keys.component.html'
})
export class ApiKeysComponent implements OnInit, OnDestroy {

  private subscriptions = new Subscription();

  entities: any[] = [];

  saving = false;
  showDialog = false;

  llmProviderOptions: SelectItem[] = [
    {label: 'Claude', value: 'CLAUDE'},
    {label: 'Groq', value: 'GROQ'},
    {label: 'Mistral', value: 'MISTRAL'},
    {label: 'OpenAI', value: 'OPENAI'},
    {label: 'SerpApi', value: 'SERPAPI'},
    {label: 'DeepInfra', value: 'DEEPINFRA'},
    {label: 'Fireworks', value: 'FIREWORKS'},
    {label: 'Cohere', value: 'COHERE'},
  ];

  selectedProvider: SelectItem = this.llmProviderOptions[0];
  keyValue = '';
  keyName = '';

  constructor(protected apiKeyService: ApiKeysService,
              protected confirmationService: ConfirmationService,
              protected messageService: MessageService) {}

  ngOnInit(): void {
     this.loadAll();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  private loadAll() {
    this.subscriptions.add(
      this.apiKeyService.query().subscribe((res) => {
        if (res.body) {
          this.entities = res.body;
        }
      })
    );
  }

  deleteKey(id: number) {
    this.confirmationService.confirm({
      message: 'Are you sure you want to revoke this key?',
      accept: () => {
        this.subscriptions.add(
          this.apiKeyService.delete(id).subscribe(() => {
            this.messageService.add({severity: 'success', summary: 'Success', detail: 'Key revoked'});
            this.loadAll();
          })
        );
      }
    });
  }

  saveKey() {
    this.saving = true;

    const params = {
      name: this.keyName,
      apiKey: this.keyValue,
      languageType: this.selectedProvider.value
    };

    this.subscriptions.add(
      this.apiKeyService
        .create(params)
        .subscribe(() => {
          this.saving = false;
          this.showDialog = false;
          this.messageService.add({severity: 'success', summary: 'Success', detail: 'Encrypted & saved'});
          this.loadAll();
        }, (res) => {
          this.showDialog = false;
          this.saving = false;
          this.messageService.add({severity: 'error', summary: 'Error', detail: res.error.detail});
        }
    ));
  }

  visit(url: string) {
    window.open(url, '_blank');
  }
}
