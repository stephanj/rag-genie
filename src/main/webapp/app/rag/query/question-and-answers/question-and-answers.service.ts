import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { QuestionResponse } from '../../../shared/model/question-response.model';

@Injectable({
  providedIn: 'root'
})
export class QuestionAndAnswersService {

  constructor(protected http: HttpClient) {
  }

  askQuestion(data: any): Observable<QuestionResponse> {
    return this.http.post<QuestionResponse>('/api/question', data);
  }
}
