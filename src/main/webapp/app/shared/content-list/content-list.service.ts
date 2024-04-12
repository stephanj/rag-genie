import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Content } from '../model/content.model';

@Injectable({
  providedIn: 'root'
})
export class ContentListService {

  resourceUrl = 'api/content';

  constructor(protected http: HttpClient) {
  }

  getContent(req?: any, filters?: any) {
    if (filters && filters.value) {
      req = { ...req, ...filters };
    }
    return this.http.get<Content[]>(this.resourceUrl, { params: req, observe: 'response' });
  }

  deleteContent(id: number) {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
