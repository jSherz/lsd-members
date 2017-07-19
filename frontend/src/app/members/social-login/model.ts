export class SocialLoginResponse {

  success: boolean;

  error?: string;

  jwt?: string;

  committeeMember: boolean;

  constructor(success: boolean, error: string, jwt: string, committeeMember: boolean) {
    this.success = success;
    this.error = error;
    this.jwt = jwt;
    this.committeeMember = committeeMember;
  }

}

export class SocialLoginRequest {

  verificationCode: string;

  constructor(verificationCode: string) {
    this.verificationCode = verificationCode;
  }

}
