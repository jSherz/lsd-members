import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import * as moment from 'moment';

import {Member, MemberAddResult, MemberEditResult, SearchResult, TextMessage} from './model';
import {BaseService, ApiKeyService} from '../utils';
import {environment} from '../../../environments/environment';


export abstract class MemberService extends BaseService {

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  abstract getMember(uuid: string): Observable<Member>

  abstract getTextMessages(uuid: string): Observable<TextMessage[]>

  abstract search(term: string): Observable<SearchResult[]>

  abstract addMember(member: Member): Observable<MemberAddResult>

  abstract editMember(member: Member): Observable<MemberEditResult>

}

@Injectable()
export class MemberServiceImpl extends MemberService {
  private baseUrl = environment.apiUrl + '/api/v1/';
  private getUrl = this.baseUrl + 'members/{{uuid}}';
  private textMessagesUrl = this.getUrl + '/text-messages';
  private memberSearchUrl = this.baseUrl + 'members/search';
  private editUrl = this.baseUrl + 'members/{{uuid}}';
  private addUrl = this.baseUrl + 'members/create';

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  getMember(uuid: string): Observable<Member> {
    return this.get(this.getUrl.replace('{{uuid}}', uuid))
      .map(r => {
        const rawMember = this.extractJson<Member>(r);

        rawMember.lastJump = rawMember.lastJump == null ? null : moment(rawMember.lastJump);
        rawMember.createdAt = rawMember.createdAt == null ? null : moment(rawMember.createdAt);
        rawMember.updatedAt = rawMember.updatedAt == null ? null : moment(rawMember.updatedAt);

        return rawMember;
      })
      .catch(this.handleError());
  }

  getTextMessages(uuid: string): Observable<TextMessage[]> {
    return this.get(this.textMessagesUrl.replace('{{uuid}}', uuid))
      .map(r => {
        const rawMessages = this.extractJson<TextMessage[]>(r);

        return rawMessages.map(message => {
          message.createdAt = message.createdAt == null ? null : moment(message.createdAt);
          message.updatedAt = message.updatedAt == null ? null : moment(message.updatedAt);

          return message;
        });
      })
      .catch(this.handleError());
  }

  search(term: string): Observable<SearchResult[]> {
    const body = {
      searchTerm: term
    };

    return this.post(this.memberSearchUrl, body)
      .map(r => this.extractJson<SearchResult[]>(r))
      .catch(this.handleError());
  }

  addMember(member: Member): Observable<MemberAddResult> {
    return this.post(this.addUrl, member)
      .catch(this.handleError());
  }

  editMember(member: Member): Observable<MemberEditResult> {
    return this.put(this.editUrl.replace('{{uuid}}', member.uuid), member)
      .catch(this.handleError());
  }

}
