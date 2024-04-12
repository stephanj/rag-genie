export interface ISignedUrl {
  value?: string;
  storedFileName?: string;
}

export class SignedUrl implements ISignedUrl {
  constructor(public value?: string, public storedFileName?: string) {}
}
