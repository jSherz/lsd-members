export class SearchResult {
  uuid: string;
  name: string;
  phoneNumber: string;
  email: string;
  chosen: boolean;

  constructor(uuid, name, phoneNumber, email) {
    this.uuid = uuid;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.chosen = false;
  }
}
