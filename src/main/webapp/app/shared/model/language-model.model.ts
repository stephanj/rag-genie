import { Entity, IEntity } from './entity.model';

export enum LanguageModelType {
  Claude = 'CLAUDE',
  Gemini = 'GEMINI',
  GPT4ALL = 'GPT4ALL',
  Groq = 'GROQ',
  LMStudio = 'LMSTUDIO',
  Mistral = 'MISTRAL',
  Ollama = 'OLLAMA',
  OpenAI = 'OPENAI',
  DeepInfra = 'DEEPINFRA',
  Fireworks = 'FIREWORKS',
  Cohere = 'COHERE'
}

export interface ILanguageModel extends IEntity<number>{
  name?: string;
  description?: string;
  version?: string;
  modelType?: LanguageModelType;
  costInput1M?: number;
  costOutput1M?: number;
  contextWindow?: number;
  tokens?: boolean;
  paramsSize?: number;
  apiKeyRequired?: boolean;
  baseUrl?: string;
  website?: string;
}

export class LanguageModel extends Entity<number> implements ILanguageModel {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public version?: string,
        public modelType?: LanguageModelType,
        public costInput1M?: number,
        public costOutput1M?: number,
        public contextWindow?: number,
        public tokens?: boolean,
        public paramsSize?: number,
        public apiKeyRequired?: boolean,
        public baseUrl?: string,
        public website?: string
    ) {
      super(id);
    }
}
