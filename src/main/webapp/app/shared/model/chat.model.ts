import {Entity, IDateEntity} from './entity.model';

export interface IChat extends IDateEntity<number> {
  userId?: number;
  userName?: string;
  question?: number;
  response?: string;
  goodResponse?: boolean;
}

export class Chat extends Entity<number> implements IChat {
  constructor(
    public id?: number,
    public userId?: number,
    public userName?: string,
    public question?: number,
    public response?: string,
    public goodResponse?: boolean) {
    super(id);
  }
}
