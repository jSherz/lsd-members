import {StrippedMember} from './';

export class CourseSpaceWithMember {
  uuid: string;
  courseUuid: string;
  number: number;
  member: StrippedMember;

  constructor(uuid: string, courseUuid: string, number: number, member: StrippedMember) {
    this.uuid = uuid;
    this.courseUuid = courseUuid;
    this.number = number;
    this.member = member;
  }
}
