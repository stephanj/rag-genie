import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { SplitterInfo } from '../model/splitter-info.model';
import { mergeMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TextSplittingService {

  constructor(private http: HttpClient) { } // Inject HttpClient if you're using it

  splitText(splitterInfo: SplitterInfo): Observable<HttpResponse<string[]>> {

    const params = new HttpParams()
      .set('contentIds', splitterInfo.contentIds.join(','))
      .set('chunkSize', splitterInfo.chunkSize)
      .set('chunkOverlap', splitterInfo.chunkOverlap)
      .set('strategy', splitterInfo.strategy)
      .set('value', splitterInfo.value);

    return this.http.get<string[]>('/api/splitter', { params, observe: 'response'});
  }

  getChunks(splitterInfo: SplitterInfo): Observable<string[]> {
    return this.splitText(splitterInfo).pipe(
      mergeMap((res: HttpResponse<string[]>) => {
        if (res.body) {
          const chunks = res.body;
          return of(chunks);
        } else {
          return of([]);
        }
      })
    );
  }
}
