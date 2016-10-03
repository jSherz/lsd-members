import * as moment from 'moment';

export class StrippedMember {
  firstName: string;
  lastName: string;
  uuid: string;
  weight: number;
  height: number;
  createdAt: moment.Moment;

  constructor(firstName: string, lastName: string, uuid: string, weight: number, height: number, createdAt: moment.Moment) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.uuid = uuid;
    this.weight = weight;
    this.height = height;
    this.createdAt = createdAt;
  }

  infoComplete() {
    return this.firstName != null && this.lastName != null && this.weight != null && this.height != null;
  }
}
