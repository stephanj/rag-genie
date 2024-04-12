import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { IEntity } from '../model/entity.model';
import { ConfirmationService, MessageService } from 'primeng/api';
import { AbstractEntityService } from './abstract-entity.service';
import { Subscription } from 'rxjs';
import { JhiEventManager } from '@ng-jhipster/service';
import { Injectable, OnDestroy } from '@angular/core';

@Injectable()
export abstract class AbstractCrud<T extends IEntity<ID>, ID> implements OnDestroy {
  // Sort fields
  sortField = 'id';
  reverse = 'asc';

  // Paging fields
  totalItems = 0;
  page = 0;
  itemsPerPage = 20;

  // Actual entity data
  entities: IEntity<ID>[] = [];

  eventSubscriber?: Subscription;

  protected constructor(
    protected entityService: AbstractEntityService<T, ID>,
    protected confirmationService: ConfirmationService,
    protected messageService: MessageService,
    protected eventManager: JhiEventManager
  ) {
    this.registerChangeInEntity();
    this.loadAll();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber != null) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  registerChangeInEntity(): void {
    this.eventSubscriber = this.eventManager.subscribe(this.eventNameToMonitor, () => this.loadAll());
  }

  abstract get eventNameToMonitor(): string;

  protected sort(): Array<string> | null {
    if (this.sortField == null) {
      return null;
    } else {
      return [this.sortField + ',' + this.reverse];
    }
  }

  loadAll(req?: any): void {
    if (req) {
      this.entityService.query(req).subscribe({
        next: (res: HttpResponse<Array<T>>) => this.paginateEntities(res.body || [], res.headers),
        error: (res: HttpErrorResponse) => this.onError(res),
      });
    } else {
      this.entityService
        .query({
          page: this.page,
          size: this.itemsPerPage,
          sort: this.sort(),
        })
        .subscribe({
          next: (res: HttpResponse<Array<T>>) => this.paginateEntities(res.body || [], res.headers),
          error: (res: HttpErrorResponse) => this.onError(res),
        });
    }
  }

  // Paging change event
  public onChange(event: any): void {
    if (event.field === undefined) {
      this.page = event.first / event.rows; // calculate the current page
      this.itemsPerPage = event.rows; // update the items per page
    } else {
      this.sortField = event.field; // update the sort field
      this.reverse = event.order === 1 ? 'desc' : 'asc'; // update the sort order
    }
    this.loadAll();
  }

  protected onError(errorMessage: any, entityName?: string): void {
    if (errorMessage.detail !== undefined && errorMessage.detail.includes('ConstraintViolationException')) {
      // Show more details when ConstraintViolationException occurs.
      this.messageService.add({
        severity: 'error',
        summary: entityName + ' can not be deleted because still used somewhere else.',
        detail: errorMessage.detail,
        sticky: true,
      });
    } else {
      this.messageService.add({ severity: 'error', summary: errorMessage.detail });
    }
  }

  protected paginateEntities(data: Array<T>, headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count') || '0');
    this.entities = data;
  }

  delete(entity: T, entityName: string, entityValue: string): void {
    this.confirmationService.confirm({
      message: 'Are you sure that you want to delete ' + entityName + ' "' + entityValue + '" ?',
      accept: () => {
        if (entity && entity.id) {
          this.entityService.delete(entity.id).subscribe( {
            next: () => {
              this.messageService.add({
                severity: 'success',
                summary: entityName + ' removed',
                detail: entityName + ' "' + entityValue + '" deleted',
              });
              this.loadAll();
            },
            error: (res: HttpErrorResponse) => this.onError(res.error, entityName),
          });
        }
      },
    });
  }
}
