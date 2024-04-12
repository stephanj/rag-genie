import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IInteraction } from '../../../shared/model/interaction.model';
import { AbstractEntityService } from '../../../shared/crud/abstract-entity.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InteractionService extends AbstractEntityService<IInteraction, number> {
  protected resourceUrl = 'api/interaction';

  constructor(protected http: HttpClient) {
    super(http);
  }

  deleteAll(): Observable<void> {
    return this.http.delete<void>(`${this.resourceUrl}/all`);
  }
}
