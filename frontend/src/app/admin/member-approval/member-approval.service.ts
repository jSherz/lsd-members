import { Injectable } from "@angular/core";
import { Http } from "@angular/http";
import { Observable } from "rxjs";

import { BaseService } from "../utils/base.service";
import { ApiKeyService } from "../utils/api-key.service";
import { Member } from "../member/model/member";

export class ApprovalResult {
  success: boolean;
  error: string;

  constructor(success: boolean, error: string) {
    this.success = success;
    this.error = error;
  }
}

export abstract class MemberApprovalService extends BaseService {
  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  abstract getMembersAwaitingApproval(): Observable<Member[]>;

  abstract getRejectedMembers(): Observable<Member[]>;

  abstract approveMember(member: Member): Observable<ApprovalResult>;

  abstract rejectMember(member: Member): Observable<ApprovalResult>;

  abstract deleteRejection(member: Member): Observable<ApprovalResult>;
}

@Injectable()
export class MemberApprovalServiceImpl extends MemberApprovalService {
  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  getMembersAwaitingApproval(): Observable<Member[]> {
    throw new Error("Method not implemented.");
  }

  getRejectedMembers(): Observable<Member[]> {
    throw new Error("Method not implemented.");
  }

  approveMember(member: Member): Observable<ApprovalResult> {
    throw new Error("Method not implemented.");
  }

  rejectMember(member: Member): Observable<ApprovalResult> {
    throw new Error("Method not implemented.");
  }

  deleteRejection(member: Member): Observable<ApprovalResult> {
    return undefined;
  }
}
