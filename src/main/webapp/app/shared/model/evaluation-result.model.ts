import { Entity, IDateEntity } from './entity.model';
import { LanguageModel } from './language-model.model';
import { Evaluation } from './evaluation.model';
import { Document } from './document.model';
import {EmbeddingModel} from './embedding-model.model';

export interface IEvaluationResult extends IDateEntity<number> {
  answer?: string;
  similarityScore?: number;
  keywordMatch?: number;
  durationInMs?: number;
  languageModel?: LanguageModel;
  embeddingModelReference?: EmbeddingModel;
  evaluation?: Evaluation;
  temperature?: number;
  maxDocuments?: number;
  minScore?: number;
  inputTokens?: number;
  outputTokens?: number;
  cost?: number;
  usedDocuments?: Document[]
}

export class EvaluationResult extends Entity<number> implements IEvaluationResult {
  constructor(
    public id?: number,
    public createdOn?: any,
    public similarityScore?: number,
    public answer?: string,
    public keywordMatch?: number,
    public durationInMs?: number,
    public temperature?: number,
    public maxDocuments?: number,
    public minScore?: number,
    public languageModel?: LanguageModel,
    public embeddingModelReference?: EmbeddingModel,
    public evaluation?: Evaluation,
    public inputTokens?: number,
    public outputTokens?: number,
    public cost?: number,
    public usedDocuments?: Document[]) {
      super(id);
  }
}
