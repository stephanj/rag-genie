import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AbstractEntityService } from '../../../shared/crud/abstract-entity.service';
import { IDocument } from '../../../shared/model/document.model';
import {createRequestOption} from '../../../shared/util/request-util';

@Injectable({
  providedIn: 'root'
})
export class DocumentService extends AbstractEntityService<IDocument, string> {

  resourceUrl = '/api/vector-document';

  constructor(protected http: HttpClient) {
    super(http);
  }

  getTotalDocumentsForUser(): Observable<HttpResponse<number>> {
    return this.http.head<number>(`${this.resourceUrl}`, { observe: 'response' });
  }

  storeChunksWithRef(contentId: number, chunks: string[], embeddingModelId: number): Observable<IDocument> {
    return this.http.post( `${this.resourceUrl}/${contentId}/${embeddingModelId}`, chunks);
  }

  find(id: string): Observable<IDocument> {
    return this.http.get<IDocument>(`${this.resourceUrl}/${id}`);
  }

  queryByDimension(dimension: string, req?: any): Observable<HttpResponse<Array<IDocument>>> {
    const options = createRequestOption(req);
    return this.http.get<Array<IDocument>>(this.resourceUrl + '/' + dimension,
      { params: options, observe: 'response' });
  }

  findByDimension(id: string, dimension: string): Observable<IDocument> {
    return this.http.get<IDocument>(`${this.resourceUrl}/${dimension}/${id}`);
  }

  filter(dimension: string, filter: string): Observable<HttpResponse<IDocument[]>> {
    return this.http.get<IDocument[]>(`${this.resourceUrl}/search?query=${filter}&dimension=${dimension}`,
      { observe: 'response' });
  }

  deleteByIdAndDimension(id: string, dimension: string): Observable<void> {
    return this.http.delete<void>(`${this.resourceUrl}/${dimension}/${id}`);
  }

  deleteAllByDimension(dimension: string): Observable<void> {
    return this.http.delete<void>(`${this.resourceUrl}/${dimension}`);
  }
}
