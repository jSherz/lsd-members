import { Injectable } from '@angular/core';
import { Http       } from '@angular/http';
import { Observable } from 'rxjs/Observable';

import { BaseService, ApiKeyService } from '../../utils';


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
export abstract class CommitteeService extends BaseService {

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  /**
   * Get any active committee members.
   */
  abstract active(): Observable<StrippedCommitteeMember[]>

}

@Injectable()
export class CommitteeServiceImpl extends CommitteeService {

  private committeeLookupUrl = 'http://localhost:8080/api/v1/committee-members/active';

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  active(): Observable<StrippedCommitteeMember[]> {
    return this.get(this.committeeLookupUrl)
      .map(this.extractJson)
      .catch(this.handleError);
  }

}
