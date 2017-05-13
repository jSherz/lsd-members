import {Inject, Injectable, InjectionToken} from '@angular/core';
import {JwtService} from './jwt.service';

export let JWT = new InjectionToken<string>('stub.jwt');

/**
 * Used to access the stored API key from anywhere in the application.
 *
 * Backed by local storage.
 */
@Injectable()
export class StubJwtService extends JwtService {

  private jwt: string;

  constructor(@Inject(JWT) jwt: string) {
    super();
    this.jwt = jwt;
  }

  getJwt(): string {
    return this.jwt;
  }

  setJwt(jwt: string) {
    this.jwt = jwt;
  }

  isAuthenticated(): boolean {
    return this.jwt != null && this.jwt.length >= 1;
  }

}
