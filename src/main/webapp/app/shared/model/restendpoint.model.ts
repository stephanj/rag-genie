
export interface IRestEndpoint {
  method?: string;
  path?: string;
  operationId?: string;
}

export class RestEndpoint implements IRestEndpoint {
  constructor(
    public method?: string,
    public path?: string,
    public operationId?: string) {}
}
