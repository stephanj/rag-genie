import { Component, OnInit } from '@angular/core';
import { DocumentService } from './document.service';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AbstractUpdateService } from '../../../shared/crud/abstract-update.component';
import { IDocument} from '../../../shared/model/document.model';

@Component({
  selector: 'genie-document-update',
  templateUrl: './document-update.component.html',
  styleUrl: './document-update.component.scss'
})
export class DocumentUpdateComponent extends AbstractUpdateService<IDocument, string> implements OnInit {

  editForm: UntypedFormGroup;

  constructor(protected documentService: DocumentService,
              protected activatedRoute: ActivatedRoute,
              protected fb: UntypedFormBuilder) {
    super(documentService);
    this.editForm = this.initForm();
  }

  ngOnInit(): void {
    this.isSaving = false;
    const id = this.activatedRoute.snapshot.paramMap.get('id');
    const dimension = this.activatedRoute.snapshot.paramMap.get('dim');
    if (id && dimension) {
      this.documentService.findByDimension(id, dimension)
        .subscribe((data) => {
          this.updateForm(data);
        });
    }
  }

  private initForm() : UntypedFormGroup {
    return this.fb.group({
      id: ['', Validators.required],
      embeddingModelName: [''],
      content: ['', Validators.required],
      contentName: ['']
    });
  }

  protected updateForm(document: IDocument): void {
    this.editForm.patchValue({
      id: document.id,
      embeddingModelName: document.embeddingModelName,
      content: document.text,
      contentName: document.contentName
    });
  }

  protected createFromForm(): IDocument {
    return {
      ...new Document(),
      id: this.editForm.get(['id'])?.value,
      text: this.editForm.get(['content'])?.value,
      contentName: this.editForm.get(['contentName'])?.value,
      embeddingModelName: this.editForm.get(['embeddingModelName'])?.value
    };
  }
}
