import { Inject, Injectable, InjectionToken } from "@angular/core";
import { JwtService } from "./jwt.service";

export let JWT = new InjectionToken<string>("stub.jwt");
export let JWT_COMMITTEE = new InjectionToken<string>("stub.jwt.committee");

/**
 * Used to access the stored API key from anywhere in the application.
 *
 * Backed by local storage.
 */
@Injectable()
export class StubJwtService extends JwtService {
  private jwt: string;
  private committeeMember: boolean;

  constructor(
    @Inject(JWT) jwt: string,
    @Inject(JWT_COMMITTEE) committeeMember: boolean
  ) {
    super();
    this.jwt = jwt;
    this.committeeMember = committeeMember;
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

  isCommitteeMember(): boolean {
    return this.committeeMember;
  }
}
