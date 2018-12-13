export class MemberAddResult {
  success: boolean;
  uuid: string;
  error: string;

  constructor(success: boolean, uuid: string, error: string) {
    this.success = success;
    this.uuid = uuid;
    this.error = error;
  }
}
