import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";

import { ApiKeyService, BaseService } from "../utils";
import { StrippedCommitteeMember } from "./stripped-committee-member";
import { environment } from "../../../environments/environment";

/**
 * A service that manages commmittee members
 */
export abstract class CommitteeService extends BaseService {
  constructor(http: HttpClient, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  /**
   * Get any active committee members.
   */
  abstract active(): Observable<StrippedCommitteeMember[]>;
}

@Injectable()
export class CommitteeServiceImpl extends CommitteeService {
  private committeeLookupUrl =
    environment.apiUrl + "/api/v1/committee-members/active";

  constructor(http: HttpClient, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  active(): Observable<StrippedCommitteeMember[]> {
    return this.get<StrippedCommitteeMember[]>(this.committeeLookupUrl).pipe(
      catchError(this.handleError())
    );
  }
}
