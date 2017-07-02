import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';

import {ApiKeyService, BaseService} from '../utils';
import {StrippedCommitteeMember} from './stripped-committee-member';
import {environment} from '../../../environments/environment';

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

  private committeeLookupUrl = environment.apiUrl + '/api/v1/committee-members/active';

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  active(): Observable<StrippedCommitteeMember[]> {
    return this.get(this.committeeLookupUrl)
      .map(r => this.extractJson<StrippedCommitteeMember[]>(r))
      .catch(this.handleError());
  }

}
