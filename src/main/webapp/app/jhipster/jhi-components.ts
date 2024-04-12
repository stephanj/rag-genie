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
import { JhiBooleanComponent, JhiItemCountComponent } from './component/';


import { JhiMaxValidatorDirective } from './directive';
import { JhiMaxbytesValidatorDirective } from './directive';
import { JhiMinValidatorDirective } from './directive';
import { JhiMinbytesValidatorDirective } from './directive';
import { JhiSortByDirective } from './directive';
import { JhiSortDirective } from './directive';
import { JhiCapitalizePipe } from '@ng-jhipster/pipe';
import { JhiFilterPipe } from '@ng-jhipster/pipe';
import { JhiKeysPipe } from './pipe/keys.pipe';
import { JhiOrderByPipe } from '@ng-jhipster/pipe';
import { JhiPureFilterPipe } from '@ng-jhipster/pipe';
import { JhiTruncateCharactersPipe } from '@ng-jhipster/pipe';
import { JhiTruncateWordsPipe } from '@ng-jhipster/pipe';
import { JhiJvmMemoryComponent } from '@ng-jhipster/component/metrics/jhi-jvm-memory.component';
import { JhiJvmThreadsComponent } from '@ng-jhipster/component/metrics/jhi-jvm-threads.component';
import { JhiMetricsHttpRequestComponent } from '@ng-jhipster/component/metrics/jhi-metrics-request.component';
import { JhiMetricsEndpointsRequestsComponent } from '@ng-jhipster/component/metrics/jhi-metrics-endpoints-requests';
import { JhiMetricsCacheComponent } from '@ng-jhipster/component/metrics/jhi-metrics-cache.component';
import { JhiMetricsDatasourceComponent } from '@ng-jhipster/component/metrics/jhi-metrics-datasource.component';
import { JhiMetricsSystemComponent } from '@ng-jhipster/component/metrics/jhi-metrics-system.component';
import { JhiMetricsGarbageCollectorComponent } from '@ng-jhipster/component/metrics/jhi-metrics-garbagecollector.component';
import { JhiThreadModalComponent } from '@ng-jhipster/component/metrics/jhi-metrics-modal-threads.component';

export const JHI_PIPES = [
    JhiCapitalizePipe,
    JhiFilterPipe,
    JhiKeysPipe,
    JhiOrderByPipe,
    JhiPureFilterPipe,
    JhiTruncateCharactersPipe,
    JhiTruncateWordsPipe
];

export const JHI_DIRECTIVES = [
    JhiMaxValidatorDirective,
    JhiMinValidatorDirective,
    JhiMaxbytesValidatorDirective,
    JhiMinbytesValidatorDirective,
    JhiSortDirective,
    JhiSortByDirective
];

export const JHI_COMPONENTS = [
    JhiItemCountComponent,
    JhiBooleanComponent,
    JhiJvmMemoryComponent,
    JhiJvmThreadsComponent,
    JhiMetricsHttpRequestComponent,
    JhiMetricsEndpointsRequestsComponent,
    JhiMetricsCacheComponent,
    JhiMetricsDatasourceComponent,
    JhiMetricsSystemComponent,
    JhiMetricsGarbageCollectorComponent,
    JhiThreadModalComponent
];
