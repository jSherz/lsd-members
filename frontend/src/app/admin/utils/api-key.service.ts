import { Injectable } from '@angular/core';

export abstract class ApiKeyService {

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

  private localStorageKey: string = 'API_KEY';

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
