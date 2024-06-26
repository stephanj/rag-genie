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
import { Component, Input } from '@angular/core';

@Component({
    selector: 'jhi-jvm-memory',
    template: `
        <h4>Memory</h4>
        <div *ngIf="!updating">
            <div *ngFor="let entry of (jvmMemoryMetrics | keys)">
                <span *ngIf="entry.value.max != -1; else other">
                    <span>{{ entry.key }}</span> ({{ entry.value.used / 1048576 | number: '1.0-0' }}M /
                    {{ entry.value.max / 1048576 | number: '1.0-0' }}M)
                </span>
                <div>Committed : {{ entry.value.committed / 1048576 | number: '1.0-0' }}M</div>
                <ng-template #other
                    ><span
                        ><span>{{ entry.key }}</span> {{ entry.value.used / 1048576 | number: '1.0-0' }}M</span
                    >
                </ng-template>
                <ngb-progressbar
                    *ngIf="entry.value.max != -1"
                    type="success"
                    [value]="(100 * entry.value.used) / entry.value.max"
                    [striped]="true"
                    [animated]="false"
                >
                    <span>{{ (entry.value.used * 100) / entry.value.max | number: '1.0-0' }}%</span>
                </ngb-progressbar>
            </div>
        </div>
    `
})
export class JhiJvmMemoryComponent {
    /**
     * object containing all jvm memory metrics
     */
      // eslint-disable-next-line @typescript-eslint/ban-types
    @Input() jvmMemoryMetrics!: {};

    /**
     * boolean field saying if the metrics are in the process of being updated
     */
    @Input() updating!: boolean;
}
