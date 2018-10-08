import {Injectable} from '@angular/core';
import { Observable, of } from 'rxjs';


import {CourseSpaceDepositPaidResponse, CourseSpaceMemberResponse} from './model';
import {CourseSpaceService} from './course-spaces.service';


@Injectable()
export class StubCourseSpaceService extends CourseSpaceService {

  constructor() {
    super(undefined, undefined);
  }

  addMember(uuid: string, memberUuid: string): Observable<CourseSpaceMemberResponse> {
    return of(new CourseSpaceMemberResponse(true, undefined));
  }

  removeMember(uuid: string, memberUuid: string): Observable<CourseSpaceMemberResponse> {
    return of(new CourseSpaceMemberResponse(true, undefined));
  }

  setDepositPaid(uuid: string, depositPaid: boolean): Observable<CourseSpaceDepositPaidResponse> {
    return of(new CourseSpaceDepositPaidResponse(true, undefined));
  }

}
