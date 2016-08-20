import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import {Observable} from "rxjs";

export class SearchResult {

  name: string;
  phoneNumber: string;
  email: string;

  constructor(name, phoneNumber, email) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.email = email;
  }

}

export abstract class MemberSearchService {

  abstract search(term: string): Observable<SearchResult[]>;

}

@Injectable()
export class MemberSearchServiceImpl extends MemberSearchService {

  constructor(private http: Http) {
    super();
  }

  search(term: string): Observable<SearchResult[]> {
    return Observable.of([
      new SearchResult('Sarah Sheppard', '+447763098707', 'SarahSheppard@dayrep.com'),
      new SearchResult('Finlay Bennett', '+447835492846', 'LillyKemp@rhyta.com'),
      new SearchResult('William Stanley', '+447945258738', 'TomIqbal@jourrapide.com'),
      new SearchResult('Jack Goodwin', '+447715587255', 'AbbieMurphy@teleworm.us'),
      new SearchResult('Lilly Kemp', '+447887869500', 'LiamDixon@jourrapide.com')
    ]);
  }

}
