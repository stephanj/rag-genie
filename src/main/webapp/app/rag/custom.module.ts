import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [
        RouterModule.forChild([
            {
                path: '',
                children: [
                    {
                      path: 'import',
                      loadChildren: () => import('./basic/import/import.module').then(m => m.ImportModule)
                    },
                    {
                      path: 'content',
                      loadChildren: () => import('./basic/content/content.module').then(m => m.ContentModule)
                    },
                    {
                      path: 'similarity',
                      loadChildren: () => import('./query/similarity/similarity.module').then(m => m.SimilarityModule)
                    },
                    {
                      path: 'questions',
                      loadChildren: () => import('./query/question-and-answers/question-and-answers.module').then(m => m.QuestionAndAnswersModule)
                    },
                    {
                      path: 'chatbot',
                      loadChildren: () => import('./query/chatbot/chatbot.module').then(m => m.GenieChatBotModule),
                    },
                    {
                      path: 'text-splitter',
                      loadChildren: () => import('./basic/text-splitter/text-splitter.module').then(m => m.GenieTextSplitterModule)
                    },
                    {
                      path: 'interaction-history',
                      loadChildren: () => import('./query/interaction-history/interaction-history.module').then(m => m.InteractionHistoryModule)
                    },
                    {
                      path: 'agent-researcher',
                      loadChildren: () => import('./agent/researcher/agent-researcher.module').then(m => m.AgentResearcherModule)
                    },
                    {
                      path: 'document',
                      loadChildren: () => import('./basic/document/document.module').then(m => m.DocumentModule)
                    },
                    {
                      path: 'visualize',
                      loadChildren: () => import('./advanced/visualize/vector-visualization.module').then(m => m.VectorVisualizationModule)
                    },
                    {
                      path: 'summary',
                      loadChildren: () => import('./advanced/summary/summary.module').then(m => m.SummaryModule)
                    },
                    {
                      path: 'chain-of-density',
                      loadChildren: () => import('./advanced/chain-of-density/chain-of-density.module').then(m => m.ChainOfDensityModule)
                    },
                    {
                      path: 'evaluation-questions',
                      loadChildren: () => import('./eval/evaluation/evaluation.module').then(m => m.EvaluationModule)
                    },
                    {
                      path: 'evaluation-results',
                      loadChildren: () => import('./eval/evaluation-results/evaluation-results.module').then(m => m.EvaluationResultsModule)
                    }
                ],
            },
        ]),
    ],
    declarations: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GenieCustomModule {}
