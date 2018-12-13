export class SignupResult {
  success: boolean;
  errors: any;

  constructor(success: boolean, errors: any) {
    this.success = success;
    this.errors = errors;
  }
}
