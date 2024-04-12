import { Routes } from '@angular/router';
import { ChatBotComponent } from './chatbot.component';

export const ChatBotRoute: Routes = [
  {
    path: '',
    component: ChatBotComponent,
    data: {
      defaultSort: 'id,asc',
      pageTitle: 'ChatBot',
      breadcrumb: 'ChatBot'
    }
  }
];
