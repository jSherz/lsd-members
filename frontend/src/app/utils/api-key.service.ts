import { Injectable } from '@angular/core';
import { Http       } from '@angular/http';

import { BaseService } from './base.service';

export abstract class ApiKeyService extends BaseService {

  constructor(http: Http) {
    super(http);
  }

  abstract getKey(): string;

  abstract setKey(apiKey: string);

  abstract isAuthenticated(): boolean;

}

/**
 * Used to access the stored API key from anywhere in the application.
 *
 * Backed by local storage.
 */
@Injectable()
export class ApiKeyServiceImpl extends ApiKeyService {

  private localStorageKey: string = "API_KEY";

  constructor(http: Http) {
    super(http);
  }

  getKey(): string {
    return localStorage.getItem(this.localStorageKey);
  }

  setKey(apiKey: string) {
    localStorage.setItem(this.localStorageKey, apiKey);
  }

  isAuthenticated(): boolean {
    return this.getKey() != null && this.getKey().length >= 1;
  }

}
