import { Component, OnDestroy } from '@angular/core';
import { JhiAlert, JhiAlertService, JhiEventManager } from '@ng-jhipster/service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'genie-alert-error',
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
 export class GenieAlertErrorComponent implements OnDestroy {
    alerts: any[];
    cleanHttpErrorListener: Subscription;
    /* eslint-disable */
    constructor(private alertService: JhiAlertService,
                private eventManager: JhiEventManager) {
        /* eslint-enable */
        this.alerts = [];

        this.cleanHttpErrorListener = eventManager.subscribe('genieApp.httpError', (response: any) => {
            const httpErrorResponse = response.content;
            switch (httpErrorResponse.status) {
                // connection refused, server not reachable
                case 0:
                    this.addErrorAlert('Server not reachable', 'error.server.not.reachable');
                    break;

                case 400:
                  this.handle400(httpErrorResponse);
                  break;

                case 404:
                    this.addErrorAlert('Not found', 'error.url.not.found');
                    break;

                default:
                    if (httpErrorResponse.error !== '' && httpErrorResponse.error.message) {
                        this.addErrorAlert(httpErrorResponse.error.message);
                    } else {
                        this.addErrorAlert(httpErrorResponse.error);
                    }
            }
        });
    }

  private handle400(httpErrorResponse: any): void {
    const arr = Object.keys(httpErrorResponse.headers);
    let errorHeader = null;
    let entityKey = null;
    arr.forEach((entry: string) => {
      if (entry.toLowerCase().endsWith('app-error')) {
        errorHeader = httpErrorResponse.headers.get(entry);
      } else if (entry.toLowerCase().endsWith('app-params')) {
        entityKey = httpErrorResponse.headers.get(entry);
      }
    });
    if (errorHeader) {
      const entityName = entityKey;
      this.addErrorAlert(errorHeader, errorHeader, {entityName});
    } else if (httpErrorResponse.error !== '' && httpErrorResponse.error.fieldErrors) {
      const fieldErrors = httpErrorResponse.error.fieldErrors;
      for (let i = 0; i < fieldErrors.length; i++) {
        const fieldError = fieldErrors[i];
        if (['Min', 'Max', 'DecimalMin', 'DecimalMax'].includes(fieldError.message)) {
          fieldError.message = 'Size';
        }
        // convert 'something[14].other[4].id' to 'something[].other[].id' so translations can be written to it
        const convertedField = fieldError.field.replace(/\[\d*\]/g, '[]');
        const fieldName = convertedField;
        this.addErrorAlert('Error on field "' + fieldName + '"', 'error.' + fieldError.message, {fieldName});
      }
    } else if (httpErrorResponse.error !== '' && httpErrorResponse.error.message) {
      this.addErrorAlert(
        httpErrorResponse.error.message,
        httpErrorResponse.error.message,
        httpErrorResponse.error.params
      );
    } else {
      this.addErrorAlert(httpErrorResponse.error);
    }
  }

  setClasses(alert: any): any {
        return {
            toast: !!alert.toast,
            [alert.position]: true
        };
    }

    ngOnDestroy(): void {
        if (this.cleanHttpErrorListener !== undefined && this.cleanHttpErrorListener !== null) {
            this.eventManager.destroy(this.cleanHttpErrorListener);
            this.alerts = [];
        }
    }

    addErrorAlert(message: string, key?: string, data?: any): void {
        message = key != null ? key : message;

        const newAlert: JhiAlert = {
            type: 'danger',
            msg: message,
            params: data,
            timeout: 5000,
            toast: this.alertService.isToast(),
            scoped: true
        };

        this.alerts.push(this.alertService.addAlert(newAlert, this.alerts));
    }
}
