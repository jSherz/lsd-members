import {Observable} from 'rxjs/Observable';
import {CommitteeStatsService} from './committee-stats.service';
import {Subject} from 'rxjs/Subject';

export class StubCommitteeStatsService extends CommitteeStatsService {

  numReceived: Subject<number> = new Subject<number>();

  constructor() {
    super(null, null, null);
  }

  getNumReceivedMessages(): Observable<number> {
    return this.numReceived;
  }

}
