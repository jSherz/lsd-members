import { Injectable      } from '@angular/core';
import { Http            } from '@angular/http';

import { Observable      } from 'rxjs/Observable';
import { ErrorObservable } from 'rxjs/observable/ErrorObservable';

/**
 * A committee member with the bare minimum of information.
 */
export class StrippedCommitteeMember {
  uuid: string;
  name: string;

  constructor (uuid: string, name: string) {
    this.uuid = uuid;
    this.name = name;
  }
}

/**
 * A service that manages commmittee members
 */
export abstract class CommitteeService {

  /**
   * Get any active committee members.
   */
  abstract active(): Observable<StrippedCommitteeMember[]>

}

@Injectable()
export class CommitteeServiceImpl extends CommitteeService {

  private committeeLookupUrl = 'http://localhost:8080/api/v1/committee-members/active';

  constructor(private http: Http) {}

  active(): Observable<StrippedCommitteeMember[]> {
    return this.http.get(this.committeeLookupUrl)
      .map(res => res.json() || {})
      .catch(this.handleError);
  }

  /**
   * Handle a generic error encountered when performing an AJAX request.
   *
   * @param err
   * @param caught
   * @returns {ErrorObservable}
   */
  private handleError<R, T>(err: any, caught: Observable<T>): ErrorObservable {
    let errMsg = (err.message) ? err.message : err.status ? `${err.status} - ${err.statusText}` : 'Server error';
    console.error(errMsg);

    return Observable.throw(new Error(errMsg));
  }

}
