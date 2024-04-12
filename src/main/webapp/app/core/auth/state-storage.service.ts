import { Injectable } from '@angular/core';
import { SessionStorageService } from 'ngx-webstorage';
import { USER_ACCOUNT_KEY } from '../../shared/constants/storage.constants';
import { Account } from '../../shared/model/account.model';

@Injectable({ providedIn: 'root' })
export class StateStorageService {
  constructor(private $sessionStorage: SessionStorageService) {}

  storeUrl(url: string): void {
    this.$sessionStorage.store('previousUrl', url);
  }

  storeAccount(account: Account | undefined): void {
    if (account) {
      this.$sessionStorage.store(USER_ACCOUNT_KEY, account);
    }
  }

  getAccount(): Account {
    return this.$sessionStorage.retrieve(USER_ACCOUNT_KEY);
  }

  resetAccount(): void {
    this.$sessionStorage.clear(USER_ACCOUNT_KEY);
  }
}
