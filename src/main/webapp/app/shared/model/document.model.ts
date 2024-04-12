import {Entity, IEntity} from './entity.model';

export interface IDocument extends IEntity<string>{
  id?: string;
  text?: string;
  embeddingModelRefId?: number;
  embeddingModelName?: string;
  contentName?: string;
  contentId?: number;
}

export class Document extends Entity<string> implements IDocument {
  constructor(
    public id?: string,
    public text?: string,
    public embeddingModelRefId?: number,
    public embeddingModelName?: string,
    public contentName?: string,
    public contentId?: number) {
    super(id);
  }
}
