import {Injectable} from '@angular/core';
import {BaseService} from '../utils/base.service';
import {Observable} from 'rxjs';
import * as moment from 'moment';
import {Http} from "@angular/http";
import {ApiKeyService} from "../utils/api-key.service";

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

export abstract class MemberViewService extends BaseService {

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  abstract getMember(uuid: string): Observable<Member>

}

@Injectable()
export class MemberViewServiceImpl extends MemberViewService {

  private url = 'http://localhost:8080/api/v1/members/{{uuid}}';

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  getMember(uuid: string): Observable<Member> {
    return this.get(this.url.replace('{{uuid}}', uuid))
      .map(r => {
        let rawMember = this.extractJson<Member>(r);

        rawMember.lastJump = rawMember.lastJump == null ? null : moment(rawMember.lastJump);
        rawMember.createdAt = rawMember.createdAt == null ? null : moment(rawMember.createdAt);
        rawMember.updatedAt = rawMember.updatedAt == null ? null : moment(rawMember.updatedAt);

        return rawMember;
      })
      .catch(this.handleError());
  }

}
