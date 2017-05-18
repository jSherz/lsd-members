export class SocialLoginUrlResponse {

  url: string;

  constructor(url: string) {
    this.url = url;
  }

}

export class SocialLoginResponse {

  success: boolean;

  error?: string;

  jwt?: string;

  constructor(success: boolean, error?: string, jwt?: string) {
    this.success = success;
    this.error = error;
    this.jwt = jwt;
  }

}

export class SocialLoginRequest {

  verificationCode: string;

  constructor(verificationCode: string) {
    this.verificationCode = verificationCode;
  }

}
