import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TableModule } from 'primeng/table';
import { HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { DatePipe, DecimalPipe} from '@angular/common';
import { RouterLink } from '@angular/router';
import { Content, IContent } from '../model/content.model';
import { TooltipModule} from 'primeng/tooltip';
import { ContentListService } from './content-list.service';
import { RequestParams } from '../model/request-param.model';

@Component({
  selector: 'genie-content-list',
  standalone: true,
  imports: [
    TableModule,
    ConfirmDialogModule,
    DatePipe,
    RouterLink,
    DecimalPipe,
    TooltipModule
  ],
  templateUrl: './content-list.component.html'
})
export class ContentListComponent implements OnInit {

  @Input() showActions = true;
  @Input() selectEnabled = false;
  @Input() multiSelect = false;
  @Input() asAdmin = false;

  @Output() contentSelection = new EventEmitter<Content>();
  @Output() contentUnselect = new EventEmitter<Content>();

  sortField = 'id';
  reverse = 'ASC';

  content!: Content;
  entities!: Content[];

  public selectedContent!: Content[];

  totalItems = 0;
  page = 0;
  itemsPerPage = 10;

  constructor(private contentListService: ContentListService,
              protected confirmationService: ConfirmationService,
              protected messageService: MessageService) {
  }

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(req?: any) {
    this.contentListService.getContent(this.getRequestParams(), req?.filters?.name)
      .subscribe({
        next: (res) => this.paginateEntities(res.body || [], res.headers),
        error: (res: HttpErrorResponse) => this.onError(res)
      });
  }

  getRequestParams(): RequestParams {
    return {
      page: this.page,
      size: this.itemsPerPage,
      sort: this.sort()
    };
  }

  protected sort(): any {
    if (this.sortField === undefined) {
      return null;
    } else {
      return [this.sortField + ',' + this.reverse];
    }
  }

  private paginateEntities(data: Array<IContent>, headers: HttpHeaders): void {
    this.totalItems = Number(headers.get('X-Total-Count') || '0');
    this.entities = data;
  }

  private onError(errorMessage: HttpErrorResponse): void {
      this.messageService.add({
        severity: 'error',
        detail: errorMessage.message,
        sticky: true
      });
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

  paginate(event: any): void {
    this.page = event.first / event.rows + 1; // calculate the current page
    this.itemsPerPage = event.rows; // update the items per page
    this.loadAll();
  }

  deleteContent(id: number, name: string): void {
    this.confirmationService.confirm({
      message: 'Please confirm removal of "' + name + '" ?',
      accept: () => {
        this.contentListService.deleteContent(id).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Content removed successfully' });
            this.loadAll();
          },
          error: () => {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Content removal failed' });
          }
        });
      }
    });
  }

  getIcon(content: IContent): string {
    if (content.contentType?.toString() === 'TEXT') {
      return 'pi pi-file';
    } else if (content.contentType?.toString() === 'HTML') {
      return 'pi pi-globe';
    } else if (content.contentType?.toString() === 'EXCEL') {
      return 'pi pi-file-excel';
    } else if (content.contentType?.toString() === 'DOC') {
      return 'pi pi-file-word';
    } else if (content.contentType?.toString() === 'PDF') {
      return 'pi pi-file-pdf';
    } else if (content.contentType?.toString() === 'CODE') {
      return 'pi pi-code';
    } else {
      return 'pi pi-file';
    }
  }

  onRowSelect(event: any) {
    this.contentSelection.emit(event.data);
  }

  onRowUnselect(event: any) {
    this.contentUnselect.emit(event.data);
  }

  loadContent($event: any) {
    this.loadAll($event);
  }

  onSelectAll() {
    this.selectedContent = this.entities;
    this.entities.forEach((content) => {
      this.contentSelection.emit(content);
    });
  }
}
