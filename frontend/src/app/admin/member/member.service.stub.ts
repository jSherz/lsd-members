import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {MemberService, Member, TextMessage, SearchResult, MemberAddResult, MemberEditResult} from './member.service';


@Injectable()
export class StubMemberService extends MemberService {

  constructor() {
    super(null, null);
  }

  getMember(uuid: string): Observable<Member> {
    return Observable.of(undefined);
  }

  getTextMessages(uuid: string): Observable<TextMessage[]> {
    return Observable.of(undefined);
  }

  search(term: string): Observable<SearchResult[]> {
    return Observable.of(undefined);
  }

  addMember(member: Member): Observable<MemberAddResult> {
    return Observable.of(undefined);
  }

  editMember(member: Member): Observable<MemberEditResult> {
    return Observable.of(undefined);
  }

}
