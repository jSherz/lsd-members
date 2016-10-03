import * as moment from 'moment';

export class CourseCreateRequest {

  date: string;
  organiserUuid: string;
  secondaryOrganiserUuid: string;
  numSpaces: number;

  constructor(date: moment.Moment, organiserUuid: string, secondaryOrganiserUuid: string, numSpaces: number) {
    this.date = date.format('YYYY-MM-DD');
    this.organiserUuid = organiserUuid;
    this.secondaryOrganiserUuid = secondaryOrganiserUuid;
    this.numSpaces = numSpaces;
  }

}
