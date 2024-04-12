import { Routes } from '@angular/router';
import { AgentResearcherComponent } from './agent-researcher.component';

export const AgentResearcherRoute: Routes = [
    {
        path: '',
        component: AgentResearcherComponent,
        data: {
            pageTitle: 'Agent Researcher',
            breadcrumb: 'Agent Researcher'
        }
    }
];
