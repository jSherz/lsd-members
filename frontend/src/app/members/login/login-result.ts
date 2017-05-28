
export class LoginResult {
  success: boolean;
  error: any;
  jwt: string;
  committeeMember: boolean;

  constructor(success: boolean, error: any, jwt: string, committeeMember: boolean) {
    this.success = success;
    this.error = error;
    this.jwt = jwt;
    this.committeeMember = committeeMember;
  }
}
