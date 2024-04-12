import {Observable} from 'rxjs';
import {HttpClient, HttpEvent, HttpResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {appConfig} from '../../../environments/environment';
import {SignedUrl} from '../model/signed-url.model';

@Injectable({ providedIn: 'root' })
export class ImageService {
  public mediaResourceUrl = appConfig.serverApiUrl +  '/api/media';

  constructor(protected http: HttpClient) {}

  getSignedUrl(fileName: string, contentType: string): Observable<HttpResponse<SignedUrl>> {
    return this.http.get<SignedUrl>(`${this.mediaResourceUrl}/${fileName}?contentType=${contentType}`, { observe: 'response' });
  }

  // eslint-disable-next-line @typescript-eslint/ban-types
  uploadMedia(url: string, blob: any): Observable<HttpEvent<object>> {
    return this.http.put(url, blob, { observe: 'events', reportProgress: true });
  }
}
