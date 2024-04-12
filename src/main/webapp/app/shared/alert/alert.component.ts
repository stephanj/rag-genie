import {Component, OnDestroy, OnInit} from '@angular/core';
import {JhiAlertService} from '@ng-jhipster/public_api';

@Component({
    selector: 'genie-alert',
    template: `
        <div class="alerts" role="alert">
            <div *ngFor="let alert of alerts" [ngClass]="setClasses(alert)">
                <ngb-alert *ngIf="alert && alert.type && alert.msg" [type]="alert.type" (close)="alert.close(alerts)">
                    <pre [innerHTML]="alert.msg"></pre>
                </ngb-alert>
            </div>
        </div>
    `
})
export class GenieAlertComponent implements OnInit, OnDestroy {
    alerts: any[] = [];

    constructor(private alertService: JhiAlertService) {}

    ngOnInit(): void {
        this.alerts = this.alertService.get();
    }

    setClasses(alert: any): any {
        return {
            toast: !!alert.toast,
            [alert.position]: true
        };
    }

    ngOnDestroy(): void {
        this.alerts = [];
    }
}
