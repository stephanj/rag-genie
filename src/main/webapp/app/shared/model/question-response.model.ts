import { DocumentResult } from './document-result.model';
import { ILanguageModel } from './language-model.model';
import { IEmbeddingModel } from './embedding-model.model';

export interface IQuestionResponse {
  id?: number;
  answer?: string;
  cost?: number;
  createdOn?: any;
  durationInMs?: number;
  languageModel?: ILanguageModel;
  embeddingModel?: IEmbeddingModel;
  allContentUsed?: boolean;
  inputTokens?: number;
  outputTokens?: number;
  costInput1M?: number;
  costOutput1M?: number;
  usedDocuments?: DocumentResult[];
}

export class QuestionResponse implements IQuestionResponse {
  constructor(
    public id?: number,
    public answer?: string,
    public cost?: number,
    public createdOn?: any,
    public durationInMs?: number,
    public languageModel?: ILanguageModel,
    public embeddingModel?: IEmbeddingModel,
    public allContentUsed?: boolean,
    public inputTokens?: number,
    public outputTokens?: number,
    public costInput1M?: number,
    public costOutput1M?: number,
    public usedDocuments?: DocumentResult[]
  ) {}
}
