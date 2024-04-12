import type {Moment} from 'moment';

export interface IEntity<ID> {
  id?: ID;
}

export interface IDateEntity<ID> extends IEntity<ID> {
  createdOn?: Moment;
}

export class Entity<ID> implements IEntity<ID> {
  constructor(public id?: ID) {}
}

