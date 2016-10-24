
export class LoginResult {
  success: boolean;
  errors: any;
  apiKey: string;

  constructor(success: boolean, errors: any, apiKey: string) {
    this.success = success;
    this.errors = errors;
    this.apiKey = apiKey;
  }
}
