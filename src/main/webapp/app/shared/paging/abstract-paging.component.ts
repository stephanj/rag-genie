import { Message, MessageService } from 'primeng/api';
import { HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import { ITEMS_PER_PAGE } from '../constants/pagination.constants';
import { RequestParams} from '../model/request-param.model';

export abstract class AbstractPagingComponent {
  msgs: Message[];

  error: any;
  previousPage: any;

  totalItems: any;
  page = 0;
  itemsPerPage: any;

  loading = false;

  reverse = 'desc';
  sortField = 'company';

  // Fix for the duplicate PrimeNG table load.
  // The table onChange() gets called twice in current setup, this counter fixes the double loadAll call
  protected secondLoad = 0;

  protected constructor(protected messageService: MessageService) {
    this.loading = true;
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.msgs = [];
  }

  protected sort(): any {
    if (this.sortField === undefined) {
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

  abstract loadAll(): void;

  protected getRequestParams(): RequestParams {
    return {
      page: this.page,
      size: this.itemsPerPage,
      sort: this.sort(),
    };
  }

  public loadingDone(): boolean {
    return !this.loading;
  }

  protected abstract processData(data: any, headers: HttpHeaders): void;

  protected onError(errorMessage: HttpErrorResponse): void {
    this.loading = false;
    if (errorMessage && errorMessage.statusText !== undefined) {
      const summaryText = errorMessage.statusText;
      const detailText = errorMessage.error.detail;
      this.messageService.add({ severity: 'error', summary: summaryText, detail: detailText });
    } else {
      this.messageService.add({ severity: 'error' });
    }
  }
}
