import * as moment from 'moment';

export class Course {
  uuid: String;
  date: moment.Moment;
  organiserUuid: String;
  secondaryOrganiserUuid: String;
  status: number;

  constructor(uuid: String, date: moment.Moment, organiserUuid: String, secondaryOrganiserUuid: String, status: number) {
    this.uuid = uuid;
    this.date = date;
    this.organiserUuid = organiserUuid;
    this.secondaryOrganiserUuid = secondaryOrganiserUuid;
    this.status = status;
  }
}
