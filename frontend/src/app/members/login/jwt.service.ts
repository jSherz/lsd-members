import {Injectable} from '@angular/core';

export abstract class JwtService {

  abstract getJwt(): string;

  abstract setJwt(jwt: string);

  abstract isAuthenticated(): boolean;

}

/**
 * Used to access the stored JWT from anywhere in the application.
 *
 * Backed by local storage.
 */
@Injectable()
export class JwtServiceImpl extends JwtService {

  private localStorageKey = 'JWT';

  getJwt(): string {
    return localStorage.getItem(this.localStorageKey);
  }

  setJwt(jwt: string) {
    localStorage.setItem(this.localStorageKey, jwt);
  }

  isAuthenticated(): boolean {
    return this.getJwt() != null && this.getJwt().length >= 1;
  }

}
