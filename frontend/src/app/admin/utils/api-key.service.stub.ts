import { Inject, Injectable, InjectionToken } from "@angular/core";
import { ApiKeyService } from "./api-key.service";

export let API_KEY = new InjectionToken<string>("stub.api.key");

/**
 * Used to access the stored API key from anywhere in the application.
 *
 * Backed by local storage.
 */
@Injectable()
export class StubApiKeyService extends ApiKeyService {
  private key: string;

  constructor(@Inject(API_KEY) key: string) {
    super();
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
