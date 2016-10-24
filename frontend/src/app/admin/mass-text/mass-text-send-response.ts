
export class MassTextSendResponse {
  success: boolean;
  error: string;
  uuid: string;

  constructor(success: boolean, error: string, uuid: string) {
    this.success = success;
    this.error = error;
    this.uuid = uuid;
  }
}
