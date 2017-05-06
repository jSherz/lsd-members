
export class LoginResult {
  success: boolean;
  error: any;
  jwt: string;

  constructor(success: boolean, error: any, jwt: string) {
    this.success = success;
    this.error = error;
    this.jwt = jwt;
  }
}
