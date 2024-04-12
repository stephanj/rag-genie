import {Component, Input, OnInit, Output} from '@angular/core';
import { RouterLink } from '@angular/router';
import { DropdownModule } from 'primeng/dropdown';
import { Subject } from 'rxjs';
import { ModelSelectItem} from '../model/model-select-item.model';
import { ILanguageModel} from '../model/language-model.model';
import { LanguageModelService} from '../../entities/language-model/language-model.service';
import {AutoCompleteModule} from 'primeng/autocomplete';

@Component({
  selector: 'genie-language-model-dropdown',
  standalone: true,
  imports: [
    RouterLink,
    DropdownModule,
    AutoCompleteModule,
  ],
  templateUrl: './language-model-dropdown.component.html'
})
export class LanguageModelDropdownComponent implements OnInit {

  @Input() multiple = false;
  @Output() languageModel = new Subject<ModelSelectItem | null>();

  modelOptions: ModelSelectItem[] = [];
  suggestedModels: ModelSelectItem[] = [];
  selectedModel: ModelSelectItem | null = null;

  constructor(private languageModelService: LanguageModelService) {
  }

  ngOnInit(): void {
    this.getLanguageModels();
  }

  modelSelected($event: any): void {
    this.selectedModel = $event.value;
    this.languageModel.next(this.selectedModel);
  }

  private getLanguageModels() {
    this.languageModelService
      .query({size: 100})
      .subscribe((res) => {
        res.body?.forEach((model: ILanguageModel) => {
          this.modelOptions.push({
            id: model.id,
            label: model.name,
            type: model.modelType,
            contextWindow: model.contextWindow,
            size: model.paramsSize,
            apiKeyRequired: model.apiKeyRequired
          });
        });

        this.modelOptions = this.modelOptions.sort((a, b) => a.type!.localeCompare(b.type!));
      });
  }

  searchModels($event: any): void {
    const searchQuery = $event.query.toLowerCase();

    this.suggestedModels = this.modelOptions.filter((model: ModelSelectItem) => {
      const labelMatch = model.label?.toLowerCase().includes(searchQuery) || false;
      const typeMatch = model.type?.toLowerCase().includes(searchQuery) || false;
      return labelMatch || typeMatch;
    });
  }

  modelCleared(): void {
    this.selectedModel = null;
    this.languageModel.next(null);
  }
}
