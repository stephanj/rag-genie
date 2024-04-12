import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Content } from '../../../shared/model/content.model';
import {ISearchQuery, SearchQuery} from '../../../shared/model/search-query.model';

@Injectable({
  providedIn: 'root'
})
export class ImportService {

  constructor(protected http: HttpClient) {
  }

  uploadText(content: Content) {
    return this.http.post('api/content/upload-text', content,
      { observe: 'response', responseType: 'text' as 'json' });
  }

  uploadUrl(content: Content) {
    return this.http.post('api/content/upload-url', content,
      { observe: 'response', responseType: 'text' as 'json' });
  }

  uploadRestData(content: Content) {
    return this.http.post('api/content/upload-rest-data', content,
      { observe: 'response', responseType: 'text' as 'json' });
  }

  getUrlMeta(params: HttpParams): any {
    return this.http.get('api/swagger', { params });
  }

  getUrlFields(params: HttpParams): any {
    return this.http.get('api/swagger/fields', { params });
  }

  getGitHub(content: Content): any {
    return this.http.post('api/content/github', content, { observe: 'response'});
  }

  uploadFile(formData: FormData): any {
    return this.http.post('/api/content/file', formData, { observe: 'response', withCredentials: true });
  }

  search(searchQuery: ISearchQuery): any {
    return this.http.post('api/content/search', searchQuery, { observe: 'response'});
  }
}
