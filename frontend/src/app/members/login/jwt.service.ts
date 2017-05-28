import {Injectable} from '@angular/core';

export abstract class JwtService {

  abstract getJwt(): string;

  abstract setJwt(jwt: string, committeeMember: boolean);

  abstract isAuthenticated(): boolean;

  abstract isCommitteeMember(): boolean;

}

/**
 * Used to access the stored JWT from anywhere in the application.
 *
 * Backed by local storage.
 */
@Injectable()
export class JwtServiceImpl extends JwtService {

  private localStorageKey = 'JWT';
  private committeeLocalStorageKey = 'IS_COMMITTEE';

  getJwt(): string {
    return localStorage.getItem(this.localStorageKey);
  }

  setJwt(jwt: string, committeeMember: boolean) {
    localStorage.setItem(this.localStorageKey, jwt);
    localStorage.setItem(this.committeeLocalStorageKey, committeeMember ? 'true' : 'false');
  }

  isAuthenticated(): boolean {
    return this.getJwt() != null && this.getJwt().length >= 1;
  }

  isCommitteeMember(): boolean {
    return localStorage.getItem(this.committeeLocalStorageKey) === 'true';
  }

}
