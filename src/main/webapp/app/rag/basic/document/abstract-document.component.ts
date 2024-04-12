import { ConfirmationService, MessageService } from 'primeng/api';
import { DocumentService } from './document.service';
import { JhiEventManager } from '@ng-jhipster/service';
import { tap } from 'rxjs/operators';
import {HttpErrorResponse, HttpHeaders, HttpResponse} from '@angular/common/http';
import { IDocument } from '../../../shared/model/document.model';
import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'genie-abstract-document',
  templateUrl: './abstract-document.component.html',
})
export class AbstractDocumentComponent implements OnInit {

  @Input() dimension!: string;

  filterValue!: string;
  searching = false;
  sortField = 'id';
  reverse = 'asc';

  // Paging fields
  totalItems = 0;
  page = 0;
  itemsPerPage = 20;

  loading = false;

  // Actual entity data
  entities: IDocument[] = [];

  constructor(protected documentService: DocumentService,
              protected confirmationService: ConfirmationService,
              protected messageService: MessageService,
              protected eventManager: JhiEventManager) {
  }

  ngOnInit(): void {
    this.loadAll(this.dimension);
  }

  loadAll(req?: any): void {
    this.loading = true;
    if (req) {
      this.documentService.queryByDimension(this.dimension, req).subscribe({
        next: (res: HttpResponse<Array<IDocument>>) => this.paginateEntities(res.body || [], res.headers),
        error: (res: HttpErrorResponse) => this.onError(res),
      });
    } else {
      this.documentService
        .queryByDimension(this.dimension, {
          page: this.page,
          size: this.itemsPerPage,
          sort: this.sort(),
        })
        .subscribe({
          next: (res: HttpResponse<Array<IDocument>>) => this.paginateEntities(res.body || [], res.headers),
          error: (res: HttpErrorResponse) => this.onError(res),
        });
    }
  }

  protected sort(): Array<string> | null {
    if (this.sortField == null) {
      return null;
    } else {
      return [this.sortField + ',' + this.reverse];
    }
  }

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

  deleteDocument(documentId: string): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete this vector item?',
      accept: () => {
        if (documentId !== undefined) {
          this.documentService.deleteByIdAndDimension(documentId, this.dimension).pipe(
            tap({
              next: () => {
                this.eventManager.broadcast('vectorDocumentListModification');
                this.loadAll();
                this.messageService.add({ severity: 'info', summary: 'Delete', detail: 'Item deleted' });
              }, error: (msg: HttpErrorResponse) => {
                this.messageService.add({
                  severity: 'error',
                  summary: 'Error',
                  sticky: true,
                  detail: msg.error
                });
              }
            })
          ).subscribe();
        }}});
  }

  deleteAll(): void {
    this.confirmationService.confirm({
      message: 'Are you sure you want to delete all vector items?',
      accept: () => {
        this.documentService.deleteAllByDimension(this.dimension).pipe(
          tap({
            next: () => {
              this.eventManager.broadcast('vectorDocumentListModification');
              this.loadAll();
              this.messageService.add({ severity: 'info', summary: 'Delete', detail: 'All items deleted' });
            },
            error: (msg: HttpErrorResponse) => {
              this.messageService.add({
                severity: 'error',
                summary: 'Error',
                sticky: true,
                detail: msg.error
              });
            }
          })
        ).subscribe();
      }});
  }

  search(): void {
    this.searching = true;
    this.documentService.filter(this.dimension, this.filterValue)
      .subscribe((res) => {
        this.paginateEntities(res.body || [], res.headers);
        this.totalItems = Number(res.headers.get('X-Total-Count')) || 0;
        const detailMsg = this.totalItems > 0 ? 'Found ' + this.totalItems + ' items' : 'No items found';
        this.messageService.add({ severity: 'info', summary: 'Search', detail: detailMsg });
      });
  }

  clearSearch(): void {
    this.searching = false;
    this.filterValue = '';
    this.loadAll();
  }


  protected onError(errorMessage: any, entityName?: string): void {
    this.loading = false;
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

  protected paginateEntities(data: Array<IDocument>, headers: HttpHeaders): void {
    this.loading = false;
    this.totalItems = Number(headers.get('X-Total-Count') || '0');
    this.entities = data;
  }
}

