import * as moment from "moment";

export class Member {
  uuid: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  email: string;
  lastJump: moment.Moment;
  weight: number;
  height: number;
  driver: boolean;
  organiser: boolean;
  createdAt: moment.Moment;
  updatedAt: moment.Moment;

  constructor(
    uuid: string,
    firstName: string,
    lastName: string,
    phoneNumber: string,
    email: string,
    lastJump: moment.Moment,
    weight: number,
    height: number,
    driver: boolean,
    organiser: boolean,
    createdAt: moment.Moment,
    updatedAt: moment.Moment
  ) {
    this.uuid = uuid;
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.lastJump = lastJump;
    this.weight = weight;
    this.height = height;
    this.driver = driver;
    this.organiser = organiser;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
