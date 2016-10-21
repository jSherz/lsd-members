import {Injectable} from '@angular/core';
import {ApiKeyService} from './api-key.service';


/**
 * Used to access the stored API key from anywhere in the application.
 *
 * Backed by local storage.
 */
@Injectable()
export class StubApiKeyService extends ApiKeyService {

  private key: string;

  constructor(key: string) {
    this.key = key;
  }

  getKey(): string {
    return this.key;
  }

  setKey(apiKey: string) {
    this.key = apiKey;
  }

  isAuthenticated(): boolean {
    return this.key != null && this.key.length >= 1;
  }

}
