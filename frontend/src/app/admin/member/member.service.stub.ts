import {Injectable} from '@angular/core';
import { Observable, of } from 'rxjs';


import {Member, TextMessage, SearchResult, MemberAddResult, MemberEditResult} from './model';
import {MemberService} from './member.service';


@Injectable()
export class StubMemberService extends MemberService {

  constructor() {
    super(null, null);
  }

  getMember(uuid: string): Observable<Member> {
    return of(undefined);
  }

  getTextMessages(uuid: string): Observable<TextMessage[]> {
    return of(undefined);
  }

  search(term: string): Observable<SearchResult[]> {
    return of(undefined);
  }

  addMember(member: Member): Observable<MemberAddResult> {
    return of(undefined);
  }

  editMember(member: Member): Observable<MemberEditResult> {
    return of(undefined);
  }

}
