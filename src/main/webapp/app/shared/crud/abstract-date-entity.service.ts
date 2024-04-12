import { AbstractEntityService } from './abstract-entity.service';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import * as moment from 'moment';
import type { IDateEntity } from '../model/entity.model';

export abstract class AbstractDateEntityService<T extends IDateEntity<ID>, ID> extends AbstractEntityService<T, ID> {
  create(entity: T): Observable<T> {
    const copy = this.convertDateFromClient(entity);
    return super.create(copy).pipe(map((res: T) => this.convertDateFromEntity(res)));
  }

  update(entity: T): Observable<T> {
    const copy = this.convertDateFromClient(entity);
    return super.update(copy).pipe(map((res: T) => this.convertDateFromEntity(res)));
  }

  find(id: ID): Observable<T> {
    return super.find(id).pipe(map((res: T) => this.convertDateFromEntity(res)));
  }

  query(req?: any): Observable<HttpResponse<Array<T>>> {
    return super.query(req).pipe(map((res: HttpResponse<Array<T>>) => this.convertWrappedDateArray(res)));
  }

  protected convertDateFromClient(e: T): T {
    return Object.assign({}, e, {
      createdOn: e.createdOn && e.createdOn.isValid() ? e.createdOn.toJSON() : null,
    });
  }

  protected convertDateFromEntity(e: T): T {
    if (e?.createdOn) {
      e.createdOn = moment(e.createdOn);
    }
    return e;
  }

  protected convertWrappedDateArray(res: HttpResponse<Array<T>>): HttpResponse<Array<T>> {
    res.body?.forEach((e: T) => this.convertDateFromEntity(e));
    return res;
  }
}
