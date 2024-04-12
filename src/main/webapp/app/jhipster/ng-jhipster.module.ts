/*
 Copyright 2016-2021 the original author or authors from the JHipster project.

 This file is part of the JHipster project, see https://www.jhipster.tech/
 for more information.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
import { CommonModule } from '@angular/common';
import { ModuleWithProviders, NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { JhiModuleConfig } from './config';
import { JHI_COMPONENTS, JHI_DIRECTIVES, JHI_PIPES } from './jhi-components';

@NgModule({
    imports: [CommonModule, NgbModule, FormsModule],
    declarations: [...JHI_PIPES, ...JHI_DIRECTIVES, ...JHI_COMPONENTS],
    exports: [...JHI_PIPES, ...JHI_DIRECTIVES, ...JHI_COMPONENTS, CommonModule]
})
export class NgJhipsterModule {
    static forRoot(moduleConfig: JhiModuleConfig): ModuleWithProviders<NgJhipsterModule> {
        return {
            ngModule: NgJhipsterModule,
            providers: [{ provide: JhiModuleConfig, useValue: moduleConfig }],
        };
    }
}
