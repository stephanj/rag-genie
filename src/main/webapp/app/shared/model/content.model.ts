import { Entity, IDateEntity } from './entity.model';
import {SwaggerField} from './swagger-field.model';

export enum ContentType {
  TEXT, HTML, JSON, PDF, DOC, MARKDOWN, CODE, EXCEL, WORD
}

export interface IContent extends IDateEntity<number> {
  name?: string;
  description?: string;
  source?: string;
  value?: string;
  contentType?: ContentType;
  metaData?: string;
  fields?: SwaggerField[];
  restTemplate?: string;
  userId?: number;
  fullName?: string;
  tokenCount?: number;
}

export class Content extends Entity<number> implements IContent {
  constructor(
    public id?: number,
    public name?: string,
    public description?: string,
    public source?: string,
    public value?: string,
    public contentType?: ContentType,
    public metaData?: string,
    public fields?: SwaggerField[],
    public restTemplate?: string,
    public userId?: number,
    public fullName?: string,
    public tokenCount?: number) {
      super(id);
  }
}
