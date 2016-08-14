import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import {Http} from "@angular/http";

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

  private handleError(error: any) {
    // In a real world app, we might use a remote logging infrastructure
    // We'd also dig deeper into the error to get a better message
    let errMsg = (error.message) ? error.message :
      error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    console.error(errMsg); // log to console instead
    return Observable.throw(errMsg);
  }

}
