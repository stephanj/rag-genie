import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
    imports: [
        RouterModule.forChild([
            {
                path: '',
                children: [
                    {
                      path: 'language-model',
                      loadChildren: () => import('./language-model/language-model.module').then(m => m.LanguageModelModule)
                    },
                    {
                      path: 'embedding-model',
                      loadChildren: () => import('./embedding-model/embedding-model.module').then(m => m.EmbeddingModelModule)
                    },
                    {
                      path: 'api-keys',
                      loadChildren: () => import('./api-keys/api-keys.module').then(m => m.ApiKeysModule)
                    }
                ],},
        ]),
    ],
    declarations: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GenieEntityModule {}
