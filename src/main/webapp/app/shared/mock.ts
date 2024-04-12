import { Account } from './model/account.model';
import { of } from 'rxjs';

export const AN_ACCOUNT: Account = {
  id: 1,
  activated: true,
  authorities: [],
  email: 'test@test',
  firstName: 'test',
  langKey: 'en',
  lastName: 'test',
  login: 'test',
  imageUrl: '',
  canUpdatePassword: false
};


export const activatedRouteMock = {
  snapshot: {
    paramMap: {
      get(_: string): string {
        return 'mockValue';  // Return mock value for route parameters
      }
    },
    queryParamMap: {
      get(_: string): string {
        return 'mockValue';  // Return mock value for query parameters
      }
    },
    data: {
      // Mock data here
    }
  },
  params: of({ id: '123' }),  // Observable of route params, change as needed
  queryParams: of({ search: 'query' }),  // Observable of query params, change as needed
  data: of({ key: 'value' })  // Observable of route data, change as needed
  // Add other properties of ActivatedRoute as needed
};
