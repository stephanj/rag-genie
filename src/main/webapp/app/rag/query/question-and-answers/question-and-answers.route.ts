import { Routes } from '@angular/router';
import { QuestionAndAnswersComponent } from './question-and-answers.component';

export const QuestionAndAnswerRoute: Routes = [
    {
        path: '',
        component: QuestionAndAnswersComponent,
        data: {
            pageTitle: 'Q&A',
            breadcrumb: 'Q&A'
        }
    }
];
