import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {CommitteeService, StrippedCommitteeMember} from './committee.service';

@Injectable()
export class StubCommitteeService extends CommitteeService {

  constructor() {
    super(null, null);
  }

  active(): Observable<StrippedCommitteeMember[]> {
    return Observable.of([]);
  }

}
