import {Injectable} from '@angular/core';
import { Observable, of } from 'rxjs';


import {StrippedCommitteeMember} from './stripped-committee-member';
import {CommitteeService} from './committee.service';


@Injectable()
export class StubCommitteeService extends CommitteeService {

  constructor() {
    super(null, null);
  }

  active(): Observable<StrippedCommitteeMember[]> {
    return of([]);
  }

}
