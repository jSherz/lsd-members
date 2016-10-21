import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {
  CourseSpaceService,
  CourseSpaceMemberResponse,
  CourseSpaceDepositPaidResponse
} from './course-spaces.service';

@Injectable()
export class StubCourseSpaceService extends CourseSpaceService {

  constructor() {
    super(undefined, undefined);
  }

  addMember(uuid: string, memberUuid: string): Observable<CourseSpaceMemberResponse> {
    return Observable.of(new CourseSpaceMemberResponse(true, undefined));
  }

  removeMember(uuid: string, memberUuid: string): Observable<CourseSpaceMemberResponse> {
    return Observable.of(new CourseSpaceMemberResponse(true, undefined));
  }

  setDepositPaid(uuid: string, depositPaid: boolean): Observable<CourseSpaceDepositPaidResponse> {
    return Observable.of(new CourseSpaceDepositPaidResponse(true, undefined));
  }

}
