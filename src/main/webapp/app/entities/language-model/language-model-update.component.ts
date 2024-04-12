import { Component, OnInit } from '@angular/core';
import {FormControl, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { LanguageModelService } from './language-model.service';
import { AbstractUpdateService } from '../../shared/crud/abstract-update.component';
import { ILanguageModel, LanguageModel, LanguageModelType } from '../../shared/model/language-model.model';
import { SelectItem } from 'primeng/api';

@Component({
  selector: 'genie-language-model-update',
  templateUrl: './language-model-update.component.html'
})
export class LanguageModelUpdateComponent extends AbstractUpdateService<ILanguageModel, number> implements OnInit {

  modelTypes: SelectItem[];
  languageModel?: ILanguageModel;
  editForm: UntypedFormGroup;
  selectedModel: LanguageModelType | undefined;
  private urlRegex = /^(?:http(s)?:\/\/)?[\w.-]+(?:\.[\w.-]+)+[\w\-._~:/?#[\]@!$&'()*+,;=]+$/;

  constructor(protected languageModelService: LanguageModelService,
              protected activatedRoute: ActivatedRoute,
              protected fb: UntypedFormBuilder) {
    super(languageModelService);
    this.editForm = this.initForm();

    this.modelTypes = Object.keys(LanguageModelType).map(key => {
      return {
        label: key,
        value: LanguageModelType[key as keyof typeof LanguageModelType]
      };
    });
  }

  private initForm(): UntypedFormGroup {
    return this.fb.group({
      id: [],
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(255)]],
      description: ['', [Validators.required, Validators.minLength(1)]],
      version: [''],
      modelType: ['', [Validators.required]],
      costInput1M: [],
      costOutput1M: [],
      contextWindow: [],
      tokens: [],
      paramsSize: [],
      apiKeyRequired: [],
      baseUrl: new FormControl('', [Validators.minLength(7), Validators.maxLength(255), Validators.pattern(this.urlRegex)]),
      website: new FormControl('', [Validators.minLength(7), Validators.maxLength(255), Validators.pattern(this.urlRegex)])
    });
  }

  ngOnInit(): void {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ languageModel }) => {
      if (languageModel) {
        this.languageModel = languageModel;
      } else {
        this.languageModel = new LanguageModel();
      }
      this.updateForm(languageModel);
    });
  }

  protected updateForm(languageModel: ILanguageModel): void {
    this.selectedModel = languageModel.modelType;
    this.editForm.patchValue({
      id: languageModel.id?.toString(),
      name: languageModel.name,
      description: languageModel.description,
      version: languageModel.version,
      modelType: languageModel.modelType,
      costInput1M: languageModel.costInput1M,
      costOutput1M: languageModel.costOutput1M,
      contextWindow: languageModel.contextWindow,
      tokens: languageModel.tokens,
      paramsSize: languageModel.paramsSize,
      apiKeyRequired: languageModel.apiKeyRequired,
      baseUrl: languageModel.baseUrl,
      website: languageModel.website
    });
  }

  protected createFromForm(): ILanguageModel {
    return {
      ...new LanguageModel(),
      id: this.editForm.get(['id'])?.value,
      name: this.editForm.get(['name'])?.value,
      description: this.editForm.get(['description'])?.value,
      version: this.editForm.get(['version'])?.value,
      modelType: this.editForm.get(['modelType'])?.value,
      costInput1M: this.editForm.get(['costInput1M'])?.value,
      costOutput1M: this.editForm.get(['costOutput1M'])?.value,
      contextWindow: this.editForm.get(['contextWindow'])?.value,
      tokens: this.editForm.get(['tokens'])?.value,
      paramsSize: this.editForm.get(['paramsSize'])?.value,
      apiKeyRequired: this.editForm.get(['apiKeyRequired'])?.value,
      baseUrl: this.editForm.get(['baseUrl'])?.value,
      website: this.editForm.get(['website'])?.value
    };
  }

  save(): void {
    this.isSaving = true;
    const theLanguageModel = this.createFromForm();

    if (theLanguageModel.id === null || theLanguageModel.id === undefined) {
      this.subscribeToSaveResponse(this.entityService.create(theLanguageModel));
    } else {
      this.subscribeToSaveResponse(this.entityService.update(theLanguageModel));
    }
  }

  onModelTypeChange(event: any): void {
    console.log('Selected model type: ' + event.value);
    this.selectedModel = event.value;
  }

  ollamaSelected(): boolean | undefined {
    return (this.selectedModel && this.selectedModel === LanguageModelType.Ollama);
  }
}
