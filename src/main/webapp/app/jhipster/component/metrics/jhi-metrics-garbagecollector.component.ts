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
    selector: 'jhi-metrics-garbagecollector',
    template: `
        <div class="row">
            <div class="col-md-4">
                <div *ngIf="garbageCollectorMetrics">
                    <span>
                        GC Live Data Size/GC Max Data Size ({{
                            garbageCollectorMetrics['jvm.gc.live.data.size'] / 1048576 | number: '1.0-0'
                        }}M / {{ garbageCollectorMetrics['jvm.gc.max.data.size'] / 1048576 | number: '1.0-0' }}M)</span
                    >
                    <ngb-progressbar
                        [max]="garbageCollectorMetrics['jvm.gc.max.data.size']"
                        [value]="garbageCollectorMetrics['jvm.gc.live.data.size']"
                        [striped]="true"
                        [animated]="false"
                        type="success"
                    >
                        <span
                            >{{
                                (100 * garbageCollectorMetrics['jvm.gc.live.data.size']) / garbageCollectorMetrics['jvm.gc.max.data.size']
                                    | number: '1.0-2'
                            }}%</span
                        >
                    </ngb-progressbar>
                </div>
            </div>
            <div class="col-md-4">
                <div *ngIf="garbageCollectorMetrics">
                    <span>
                        GC Memory Promoted/GC Memory Allocated ({{
                            garbageCollectorMetrics['jvm.gc.memory.promoted'] / 1048576 | number: '1.0-0'
                        }}M / {{ garbageCollectorMetrics['jvm.gc.memory.allocated'] / 1048576 | number: '1.0-0' }}M)</span
                    >
                    <ngb-progressbar
                        [max]="garbageCollectorMetrics['jvm.gc.memory.allocated']"
                        [value]="garbageCollectorMetrics['jvm.gc.memory.promoted']"
                        [striped]="true"
                        [animated]="false"
                        type="success"
                    >
                        <span
                            >{{
                                (100 * garbageCollectorMetrics['jvm.gc.memory.promoted']) /
                                    garbageCollectorMetrics['jvm.gc.memory.allocated'] | number: '1.0-2'
                            }}%</span
                        >
                    </ngb-progressbar>
                </div>
            </div>
            <div class="col-md-4">
                <div class="row" *ngIf="garbageCollectorMetrics">
                    <div class="col-md-9">Classes loaded</div>
                    <div class="col-md-3 text-right">{{ garbageCollectorMetrics.classesLoaded }}</div>
                </div>
                <div class="row" *ngIf="garbageCollectorMetrics">
                    <div class="col-md-9">Classes unloaded</div>
                    <div class="col-md-3 text-right">{{ garbageCollectorMetrics.classesUnloaded }}</div>
                </div>
            </div>
            <div class="table-responsive" *ngIf="!updating && garbageCollectorMetrics">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th></th>
                            <th class="text-right">Count</th>
                            <th class="text-right">Mean</th>
                            <th class="text-right">Min</th>
                            <th class="text-right">p50</th>
                            <th class="text-right">p75</th>
                            <th class="text-right">p95</th>
                            <th class="text-right">p99</th>
                            <th class="text-right">Max</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>jvm.gc.pause</td>
                            <td class="text-right">{{ garbageCollectorMetrics['jvm.gc.pause'].count }}</td>
                            <td class="text-right">{{ garbageCollectorMetrics['jvm.gc.pause'].mean | number: '1.0-3' }}</td>
                            <td class="text-right">{{ garbageCollectorMetrics['jvm.gc.pause']['0.0'] | number: '1.0-3' }}</td>
                            <td class="text-right">{{ garbageCollectorMetrics['jvm.gc.pause']['0.5'] | number: '1.0-3' }}</td>
                            <td class="text-right">{{ garbageCollectorMetrics['jvm.gc.pause']['0.75'] | number: '1.0-3' }}</td>
                            <td class="text-right">{{ garbageCollectorMetrics['jvm.gc.pause']['0.95'] | number: '1.0-3' }}</td>
                            <td class="text-right">{{ garbageCollectorMetrics['jvm.gc.pause']['0.99'] | number: '1.0-3' }}</td>
                            <td class="text-right">{{ garbageCollectorMetrics['jvm.gc.pause'].max | number: '1.0-3' }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    `
})
export class JhiMetricsGarbageCollectorComponent {
    /**
     * object containing garbage collector related metrics
     */
    @Input() garbageCollectorMetrics: any;

    /**
     * boolean field saying if the metrics are in the process of being updated
     */
    @Input() updating!: boolean;
}
