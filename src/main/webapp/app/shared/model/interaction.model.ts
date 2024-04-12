import { Entity, IDateEntity } from './entity.model';
import type {Moment} from 'moment';
import { LanguageModel } from './language-model.model';
import {EmbeddingModel} from './embedding-model.model';

export interface IInteraction extends IDateEntity<number> {
  question?: string;
  answer?: string;
  durationInMs?: number;
  inputTokens?: number;
  outputTokens?: number;
  cost?: number;
  vote?: string;
  userId?: number;
  languageModel?: LanguageModel;
  embeddingModel?: EmbeddingModel;
}

export class Interaction extends Entity<number> implements IInteraction {
  constructor(
    public id?: number,
    public createdOn?: Moment,
    public question?: string,
    public answer?: string,
    public durationInMs?: number,
    public inputTokens?: number,
    public outputTokens?: number,
    public cost?: number,
    public vote?: string,
    public userId?: number,
    public languageModel?: LanguageModel,
    public embeddingModel?: EmbeddingModel) {
    super(id);
  }
}
