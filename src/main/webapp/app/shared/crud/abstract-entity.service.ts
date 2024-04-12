import { IEntity } from '../model/entity.model';
import { Observable } from 'rxjs';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { createRequestOption } from '../util/request-util';

export abstract class AbstractEntityService<T extends IEntity<ID>, ID> {
  protected abstract resourceUrl:string;

  protected constructor(protected http: HttpClient) {}

  create(entity: IEntity<ID>): Observable<T> {
    return this.http.post<T>(this.resourceUrl, entity);
  }

  update(entity: IEntity<ID>): Observable<T> {
    return this.http.put<T>(this.resourceUrl, entity);
  }

  find(id: ID): Observable<T> {
    return this.http.get<T>(`${this.resourceUrl}/${id}`);
  }

  query(req?: any): Observable<HttpResponse<Array<T>>> {
    const options = createRequestOption(req);
    return this.http.get<Array<T>>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: ID): Observable<void> {
    return this.http.delete<void>(`${this.resourceUrl}/${id}`);
  }
}
