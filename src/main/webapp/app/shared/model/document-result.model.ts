import { Entity, IEntity } from './entity.model';

export enum DocumentType {
  CONTENT, WEB_SEARCH
}

export interface IDocumentResult extends IEntity<string> {
  content?: string;
  style?: string;
  score?: number;
  allDocs?: boolean;
  docType?: DocumentType;
}

export class DocumentResult extends Entity<string> implements IDocumentResult {
  constructor(
    public id?: string,
    public content?: string,
    public style?: string,
    public score?: number,
    public allDocs?: boolean,
    public docType?: DocumentType) {
    super(id);
  }
}
