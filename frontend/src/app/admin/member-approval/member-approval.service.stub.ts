import { Observable } from "rxjs";

import {
  ApprovalResult,
  MemberApprovalService
} from "./member-approval.service";
import { Member } from "../member/model/member";

export class MemberApprovalServiceStub extends MemberApprovalService {
  constructor() {
    super(null, null);
  }

  getMembersAwaitingApproval(): Observable<Member[]> {
    return undefined;
  }

  getRejectedMembers(): Observable<Member[]> {
    return undefined;
  }

  approveMember(member: Member): Observable<ApprovalResult> {
    return undefined;
  }

  rejectMember(member: Member): Observable<ApprovalResult> {
    return undefined;
  }

  deleteRejection(member: Member): Observable<ApprovalResult> {
    return null;
  }
}
