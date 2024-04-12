import {IEntity} from '../model/entity.model';
import {AbstractEntityService} from './abstract-entity.service';
import {Observable} from 'rxjs';
import {UntypedFormGroup} from '@angular/forms';
import {MessageService} from 'primeng/api';
import {HttpErrorResponse} from '@angular/common/http';

export abstract class AbstractUpdateService<T extends IEntity<ID>, ID> {

  entity?: IEntity<ID>;
  isSaving = false;

  abstract editForm: UntypedFormGroup;

  protected constructor(
    protected entityService: AbstractEntityService<T, ID>,
    protected messageService?: MessageService) {
  }

  protected abstract createFromForm(): T;

  protected abstract updateForm(entity: T): void;

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const entity = this.createFromForm();
    if (entity.id !== undefined) {
      this.subscribeToSaveResponse(this.entityService.update(entity));
    } else {
      this.subscribeToSaveResponse(this.entityService.create(entity));
    }
  }

  protected subscribeToSaveResponse(result: Observable<T>): void {
    result.subscribe({
        next: () => this.onSaveSuccess(),
        error: (err: HttpErrorResponse) => this.onError(err),
        complete: () => this.isSaving = false
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onError(error: HttpErrorResponse): void {
    if (this.messageService) {
      this.messageService.add({severity: 'error', summary: error.message});
    } else {
      console.error(error);
    }
  }
}
