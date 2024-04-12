import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { EvaluationService } from './evaluation.service';
import { AbstractUpdateService } from '../../../shared/crud/abstract-update.component';
import { Evaluation, IEvaluation } from '../../../shared/model/evaluation.model';

@Component({
  selector: 'genie-evaluation-update',
  templateUrl: './evaluation-update.component.html'
})
export class EvaluationUpdateComponent extends AbstractUpdateService<IEvaluation, number> implements OnInit {

  evaluation?: IEvaluation;

  editForm: UntypedFormGroup;

  constructor(protected evaluationService: EvaluationService,
              protected activatedRoute: ActivatedRoute,
              protected fb: UntypedFormBuilder) {
    super(evaluationService);
    this.editForm = this.initForm();
  }

  private initForm(): UntypedFormGroup {
    return this.fb.group({
      id: [],
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
      question: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
      answer: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]],
      keywords: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ evaluation }) => {
      if (evaluation) {
        this.evaluation = evaluation;
      } else {
        this.evaluation = new Evaluation();
      }
      this.updateForm(evaluation);
    });
  }

  protected updateForm(evaluation: IEvaluation): void {
    this.editForm.patchValue({
      id: evaluation.id?.toString(),
      name: evaluation.name,
      question: evaluation.question,
      answer: evaluation.answer,
      keywords: evaluation.keywords
    });
  }

  protected createFromForm(): IEvaluation {
    return {
      ...new Evaluation(),
      id: this.editForm.get(['id'])?.value,
      name: this.editForm.get(['name'])?.value,
      question: this.editForm.get(['question'])?.value,
      answer: this.editForm.get(['answer'])?.value,
      keywords: this.editForm.get(['keywords'])?.value
    };
  }

  save(): void {
    this.isSaving = true;
    const evaluation = this.createFromForm();

    if (evaluation.id === null || evaluation.id === undefined) {
      this.subscribeToSaveResponse(this.entityService.create(evaluation));
    } else {
      this.subscribeToSaveResponse(this.entityService.update(evaluation));
    }
  }
}
