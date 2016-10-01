import {Injectable} from '@angular/core';
import {BaseService} from '../utils/base.service';
import {Observable} from 'rxjs';
import * as moment from 'moment';
import {Http} from '@angular/http';
import {ApiKeyService} from '../utils/api-key.service';

export class Member {
  uuid: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  email: string;
  lastJump: moment.Moment;
  weight: number;
  height: number;
  driver: boolean;
  organiser: boolean;
  createdAt: moment.Moment;
  updatedAt: moment.Moment;

  constructor(uuid: string, firstName: string, lastName: string, phoneNumber: string, email: string, lastJump: moment.Moment,
              weight: number, height: number, driver: boolean, organiser: boolean, createdAt: moment.Moment,
              updatedAt: moment.Moment) {
    this.uuid = uuid;
    this.firstName = firstName;
    this.lastName = lastName;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.lastJump = lastJump;
    this.weight = weight;
    this.height = height;
    this.driver = driver;
    this.organiser = organiser;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}

export class TextMessage {
  uuid: string;
  memberUuid: string;
  massTextUuid: string;
  status: number;
  toNumber: string;
  fromNumber: string;
  message: string;
  externalId: string;
  createdAt: moment.Moment;
  updatedAt: moment.Moment;

  constructor(uuid: string, memberUuid: string, massTextUuid: string, status: number, toNumber: string, fromNumber: string,
              message: string, externalId: string, createdAt: moment.Moment, updatedAt: moment.Moment) {
    this.uuid = uuid;
    this.memberUuid = memberUuid;
    this.massTextUuid = massTextUuid;
    this.status = status;
    this.toNumber = toNumber;
    this.fromNumber = fromNumber;
    this.message = message;
    this.externalId = externalId;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}

export class SearchResult {

  uuid: string;
  name: string;
  phoneNumber: string;
  email: string;
  chosen: boolean;

  constructor(uuid, name, phoneNumber, email) {
    this.uuid = uuid;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.chosen = false;
  }

}

export class MemberEditResult {
  success: boolean;
  error: string;

  constructor(success: boolean, error: string) {
    this.success = success;
    this.error = error;
  }
}

export abstract class MemberService extends BaseService {

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  abstract getMember(uuid: string): Observable<Member>

  abstract getTextMessages(uuid: string): Observable<TextMessage[]>

  abstract search(term: string): Observable<SearchResult[]>

  abstract editMember(member: Member): Observable<MemberEditResult>

}

@Injectable()
export class MemberServiceImpl extends MemberService {

  private baseUrl = 'http://localhost:8080/api/v1/';
  private getUrl = this.baseUrl + 'members/{{uuid}}';
  private textMessagesUrl = this.getUrl + '/text-messages';
  private memberSearchUrl = this.baseUrl + 'members/search';
  private editUrl = this.baseUrl + 'members/{{uuid}}';

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  getMember(uuid: string): Observable<Member> {
    return this.get(this.getUrl.replace('{{uuid}}', uuid))
      .map(r => {
        let rawMember = this.extractJson<Member>(r);

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
        let rawMessages = this.extractJson<TextMessage[]>(r);

        return rawMessages.map(message => {
          message.createdAt = message.createdAt == null ? null : moment(message.createdAt);
          message.updatedAt = message.updatedAt == null ? null : moment(message.updatedAt);

          return message;
        });
      })
      .catch(this.handleError());
  }

  search(term: string): Observable<SearchResult[]> {
    let body = {
      searchTerm: term
    };

    return this.post(this.memberSearchUrl, body)
      .map(r => this.extractJson<SearchResult[]>(r))
      .catch(this.handleError());
  }

  editMember(member: Member): Observable<MemberEditResult> {
    return this.put(this.editUrl.replace('{{uuid}}', member.uuid), member)
      .catch(this.handleError());
  }

}
