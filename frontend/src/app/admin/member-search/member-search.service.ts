import { Injectable } from '@angular/core';
import { Http       } from '@angular/http';
import { Observable } from 'rxjs';

import { BaseService } from '../../utils/base.service';

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

export abstract class MemberSearchService extends BaseService {

  constructor(http: Http) {
    super(http);
  }

  abstract search(term: string): Observable<SearchResult[]>;

}

@Injectable()
export class MemberSearchServiceImpl extends MemberSearchService {

  private memberSearchUrl = 'http://localhost:8080/api/v1/members/search';

  constructor(http: Http) {
    super(http);
  }

  search(term: string): Observable<SearchResult[]> {
    let body = {
      searchTerm: term
    };

    return this.post(this.memberSearchUrl, body)
      .map(this.extractJson)
      .catch(this.handleError);
  }

}
