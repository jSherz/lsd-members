import { Observable, Subject } from "rxjs";

import { DashboardStatsService } from "./";

export class StubDashboardStatsService extends DashboardStatsService {
  numReceived: Subject<number> = new Subject<number>();

  constructor() {
    super(null, null, null);
  }

  getNumReceivedMessages(): Observable<number> {
    return this.numReceived;
  }
}
