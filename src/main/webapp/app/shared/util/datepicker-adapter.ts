/**
 * Angular bootstrap Date adapter
 */
import {Injectable} from '@angular/core';
import {NgbDateAdapter, NgbDateStruct} from '@ng-bootstrap/ng-bootstrap';
import * as moment from 'moment';
import {Moment} from 'moment';

@Injectable()
export class NgbDateMomentAdapter extends NgbDateAdapter<Moment> {
    fromModel(date: Moment): NgbDateStruct | null {
        if (date != null && moment.isMoment(date) && date.isValid()) {
            return { year: date.year(), month: date.month() + 1, day: date.date() };
        }
        return null;
    }

    toModel(date: NgbDateStruct): Moment | null {
        return date ? moment(date.year + '-' + date.month + '-' + date.day, 'YYYY-MM-DD') : null;
    }
}
