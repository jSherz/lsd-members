
export class MemberEditResult {
  success: boolean;
  error: string;

  constructor(success: boolean, error: string) {
    this.success = success;
    this.error = error;
  }
}
