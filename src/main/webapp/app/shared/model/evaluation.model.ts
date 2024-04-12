import { Entity, IEntity } from './entity.model';

export interface IEvaluation extends IEntity<number> {
  name?: string;
  question?: string;
  answer?: string;
  keywords?: string[];
}

export class Evaluation extends Entity<number> implements IEvaluation {
  constructor(
    public id?: number,
    public name?: string,
    public question?: string,
    public answer?: string,
    public keywords?: string[]) {
      super(id);
  }
}
