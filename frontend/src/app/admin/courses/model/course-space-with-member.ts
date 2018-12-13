import { StrippedMember } from "./";

export class CourseSpaceWithMember {
  uuid: string;
  courseUuid: string;
  number: number;
  member: StrippedMember;
  depositPaid: boolean;

  constructor(
    uuid: string,
    courseUuid: string,
    number: number,
    member: StrippedMember,
    depositPaid: boolean
  ) {
    this.uuid = uuid;
    this.courseUuid = courseUuid;
    this.number = number;
    this.member = member;
    this.depositPaid = depositPaid;
  }
}
