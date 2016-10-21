import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {MemberService, Member, TextMessage} from './member.service';


@Injectable()
export class StubMemberService extends MemberService {

  constructor() {
    super(undefined, undefined);
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
