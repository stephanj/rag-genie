import {Component, OnDestroy, OnInit} from '@angular/core';
import {JhiEventManager} from '@ng-jhipster/service';
import {ConfirmationService, MessageService} from 'primeng/api';
import { AbstractCrud } from '../../../shared/crud/abstract-list.component';
import { ModelSelectItem } from '../../../shared/model/model-select-item.model';
import { EvaluationService } from './evaluation.service';
import { LanguageModelService } from '../../../entities/language-model/language-model.service';
import { IEvaluation } from '../../../shared/model/evaluation.model';
import { Router } from '@angular/router';
import { HttpParams } from '@angular/common/http';
import {IEmbeddingModel} from '../../../shared/model/embedding-model.model';
import {EmbeddingModelService} from '../../../entities/embedding-model/embedding-model.service';

@Component({
  selector: 'genie-evaluation',
  templateUrl: './evaluation.component.html',
  styleUrls: ['./evaluation.component.scss']
})
export class EvaluationComponent extends AbstractCrud<IEvaluation, number> implements OnInit, OnDestroy {

  showDialog = false;

  selectedModels: ModelSelectItem[] = [];
  embeddingModels: IEmbeddingModel[] = [];
  embeddingModel: IEmbeddingModel | undefined;

  temperature = 0.7;       // Temperature
  minScore = 60;           // Minimum similarity score
  maxOutputTokens = 2000;  // Maximum output tokens to generate
  size = 5;                // maximum documents to use for evaluation
  rerankAnswers = false;

  selectedQuestions: IEvaluation[] = [];

  evaluating = false;

  constructor(
    protected evaluationService: EvaluationService,
    protected languageModelService: LanguageModelService,
    protected confirmationService: ConfirmationService,
    protected embeddingModelService: EmbeddingModelService,
    protected messageService: MessageService,
    protected router: Router,
    protected eventManager: JhiEventManager) {
    super(
      evaluationService,
      confirmationService,
      messageService,
      eventManager);
  }

  ngOnInit(): void {
    this.getEmbeddingModels();
  }

  get eventNameToMonitor(): string {
    return 'evaluationListModification';
  }

  getEmbeddingModels(): void {
    this.embeddingModelService.usedModels()
      .subscribe((res) => {
        if (res.body) {
          this.embeddingModels = res.body;
        }
      });
  }

  evaluate(): void {
    this.evaluating = true;
    const modelIds: number[] = this.selectedModels.map((model: ModelSelectItem) => {
      if (model && model.id !== undefined) {
        return model.id;
      }
      return -1;
    });

    if (modelIds && modelIds.length > 0 &&
        this.embeddingModel && this.embeddingModel.id) {

      const params = new HttpParams()
        .set('models', modelIds.join(','))
        .set('temperature', this.temperature.toString())
        .set('minScore', this.minScore / 100)
        .set('maxOutputTokens', this.maxOutputTokens.toString())
        .set('size', this.size.toString())
        .set('evaluations', this.selectedQuestions.map(e => e.id).join(','))
        .set('embedId', this.embeddingModel.id)
        .set('rerank', this.rerankAnswers.toString());

      this.evaluationService
        .startEvaluation(params)
        .subscribe(() => {
          this.evaluating = false;
          this.messageService.add({severity: 'success', summary: 'Evaluation finished'});
          this.showDialog = false;
          this.router.navigate(['/evaluation-results']);
      });
    }
  }

  addSelectedQuestion(event: any): void {
    this.selectedQuestions.push(event.data);
  }

  removeSelectedQuestion(event: any): void {
    this.selectedQuestions = this.selectedQuestions.filter((q) => q.id !== event.data.id);
  }

  onSelectAll(): void {
    this.selectedQuestions = this.entities;
  }

  onSelect(event: any): void {
    if (event == null) {
      this.selectedModels = [];
      return;
    }
    this.selectedModels.push(event);
  }
}
