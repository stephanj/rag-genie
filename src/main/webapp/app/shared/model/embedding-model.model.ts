import { Entity, IEntity } from './entity.model';

export interface IEmbeddingModel extends IEntity<number>{
  name?: string;
  slug?: string;
  dimSize?: number;
  maxTokens?: number;
  costUsage1M?: number;
  apiKeyRequired?: boolean;
  website?: string;
}

export class EmbeddingModel extends Entity<number> implements IEmbeddingModel {
    constructor(
        public id?: number,
        public name?: string,
        public slug?: string,
        public dimSize?: number,
        public maxTokens?: number,
        public costUsage1M?: number,
        public apiKeyRequired?: boolean,
        public website?: string
    ) {
      super(id);
    }
}
