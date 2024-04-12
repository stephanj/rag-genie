import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { EvaluationResult, IEvaluationResult } from '../../../shared/model/evaluation-result.model';
import { IDocument } from '../../../shared/model/document.model';

@Component({
  selector: 'genie-evaluation-results-view',
  templateUrl: './evaluation-results-view.component.html'
})
export class EvaluationResultsViewComponent implements OnInit {

  evaluationResult?: IEvaluationResult;

  editForm: UntypedFormGroup;

  missingKeywords: string[] = [];
  documents: IDocument[] = [];

  constructor(protected activatedRoute: ActivatedRoute,
              protected messageService: MessageService,
              protected fb: UntypedFormBuilder) {
    this.editForm = this.initForm();
  }

  private initForm(): UntypedFormGroup {
    return this.fb.group({
      id: [],
      createdOn: [],
      question: [''],
      answer: [''],
      model_answer: [],
      keywordMatch: [],
      keywords: [],
      similarityScore: [],
      modelName: [''],
      inputTokens: [],
      outputTokens: [],
      cost: [],
      embeddingName: []
    });
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ evaluationResult }) => {
      if (evaluationResult) {
        this.evaluationResult = evaluationResult;
        this.documents = evaluationResult.usedDocuments || [];
      } else {
        this.evaluationResult = new EvaluationResult();
      }
      this.updateForm(evaluationResult);

      this.findMissingKeywords();
    });
  }

  protected updateForm(response: IEvaluationResult): void {
    this.editForm.patchValue({
      id: response.id?.toString(),
      createdOn: response.createdOn,
      modelName: response.languageModel?.name,
      question: response.evaluation?.question,
      keywords: response.evaluation?.keywords,
      answer: response.evaluation?.answer,
      model_answer: response.answer,
      keywordMatch: response.keywordMatch,
      similarityScore: response.similarityScore,
      inputTokens: response.inputTokens,
      outputTokens: response.outputTokens,
      cost: response.cost,
      embeddingName: response.embeddingModelReference?.name
    });
  }

  previousState(): void {
    window.history.back();
  }

  findMissingKeywords(): void {
    if (this.evaluationResult?.evaluation?.keywords && this.evaluationResult?.answer) {
      // Normalize the text: remove punctuation (except % to preserve percentages), convert to lowercase, and split into words.
      const wordsInText = this.evaluationResult.answer.replace(/[.,]/g, '').toLowerCase().split(/\s+/);

      // Assuming this.evaluationResult.evaluation.keywords is already an array of strings
      const keywordsNotInText: string[] = [];

      this.evaluationResult.evaluation.keywords
        .map(keyword => {
          keyword.split(',').forEach(value => {
            if (!wordsInText.toString().toLowerCase().includes(value.toLowerCase())) {
              keywordsNotInText.push(value);
            }
          });
        });

      this.missingKeywords = keywordsNotInText;
    }
  }
}
