import firebase from 'firebase/compat';
import User = firebase.User;
import {Entity, IEntity} from './entity.model';

export interface IUser extends IEntity<number> {
  id?: number;
  login?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  activated: boolean;
  langKey?: string;
  authorities?: any[];
  createdBy?: string;
  createdDate?: Date;
  lastModifiedBy?: string;
  lastModifiedDate?: Date;
  password?: string;
  provider?: string;
  imageUrl?: string;
}

export class GenieUser extends Entity<number> implements IUser {
  constructor(
    public id?: number,
    public login?: string,
    public firstName?: string,
    public lastName?: string,
    public email?: string,
    public activated= false,
    public langKey?: string,
    public authorities?: any[],
    public createdBy?: string,
    public createdDate?: Date,
    public lastModifiedBy?: string,
    public lastModifiedDate?: Date,
    public password?: string,
    public provider?: string,
    public imageUrl?: string
  ) {
    super(id);
  }
}

export type FirebaseUser = User;

export type AuthState = 'LOGIN' | 'SIGN_UP' | 'EMAIL_VERIFICATION_CODE'
  | 'AUTHENTICATED' | 'RESET_PASSWORD' | 'RESET_PASSWORD_VERIFICATION_CODE'
  | 'INIT_PROFILE' | 'INTERNAL_ACCOUNT_AUTHENTICATED' | 'SET_NEW_PASSWORD' | 'RESET_PASSWORD_COMPLETE';
