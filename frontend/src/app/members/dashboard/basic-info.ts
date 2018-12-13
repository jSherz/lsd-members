export class BasicInfo {
  uuid: string;

  firstName: string;

  lastName: string;

  createdAt: string;

  constructor(
    uuid: string,
    firstName: string,
    lastName: string,
    createdAt: string
  ) {
    this.uuid = uuid;
    this.firstName = firstName;
    this.lastName = lastName;
    this.createdAt = createdAt;
  }
}
