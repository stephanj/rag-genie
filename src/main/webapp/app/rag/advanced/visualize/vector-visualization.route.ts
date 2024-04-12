import { Routes } from '@angular/router';
import { VectorVisualizationComponent } from './vector-visualization.component';

export const GenieVectorVisualizationRoute: Routes = [
    {
        path: ':dimension',
        component: VectorVisualizationComponent,
        data: {
            pageTitle: 'Visualize',
            breadcrumb: 'Visualize'
        }
    },

];
