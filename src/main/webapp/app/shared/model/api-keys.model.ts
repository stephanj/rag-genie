import {Entity, IEntity} from './entity.model';
import {LanguageModelType} from './language-model.model';

export interface IUserApiKey extends IEntity<number> {
  name?: string,
  keyMask?: string,
  languageType?: LanguageModelType;
  apiKey?: string;
  lastUsed?: Date;
  createdDate?: Date;
}

export class UserApiKey extends Entity<number> implements IUserApiKey {
  constructor(
    public id?: number,
    public name?: string,
    public keyMask?: string,
    public languageType?: LanguageModelType,
    public apiKey?: string,
    public createdDate?: Date) {
    super(id);
  }
}
