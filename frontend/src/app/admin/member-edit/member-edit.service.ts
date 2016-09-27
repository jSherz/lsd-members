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

export class MemberEditResult {
  success: boolean;
  error: string;

  constructor(success: boolean, error: string) {
    this.success = success;
    this.error = error;
  }
}

export abstract class MemberEditService extends BaseService {

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  abstract editMember(member: Member): Observable<MemberEditResult>

}

@Injectable()
export class MemberEditServiceImpl extends MemberEditService {

  private editUrl = 'http://localhost:8080/api/v1/members/{{uuid}}';

  constructor(http: Http, apiKeyService: ApiKeyService) {
    super(http, apiKeyService);
  }

  editMember(member: Member): Observable<MemberEditResult> {
    return this.put(this.editUrl.replace('{{uuid}}', member.uuid), member)
      .catch(this.handleError());
  }

}
