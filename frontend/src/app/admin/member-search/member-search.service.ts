import { Injectable } from '@angular/core';
import {Http, Headers, RequestOptions, Response} from "@angular/http";
import {Observable} from "rxjs";
import {ErrorObservable} from "rxjs/observable/ErrorObservable";

export class SearchResult {

  uuid: string;
  name: string;
  phoneNumber: string;
  email: string;

  constructor(uuid, name, phoneNumber, email) {
    this.uuid = uuid;
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

  private memberSearchUrl = 'http://localhost:8080/api/v1/members/search';

  constructor(private http: Http) {
    super();
  }

  search(term: string): Observable<SearchResult[]> {
    let body = {
      searchTerm: term
    };

    return this.postAsJson(this.memberSearchUrl, body)
      .map(this.extractJson)
      .catch(this.handleError);
  }

  /**
   * Extract the JSON body of a response.
   *
   * @param res
   * @returns
   */
  private extractJson(res: Response): SearchResult[] {
    let body = res.json();
    return body || [];
  }

  /**
   * Handle a generic error encountered when performing an AJAX request.
   *
   * @param err
   * @param caught
   * @returns {ErrorObservable}
   */
  private handleError<R, T>(err: any, caught: Observable<T>): ErrorObservable {
    let errMsg = (err.message) ? err.message : err.status ? `${err.status} - ${err.statusText}` : 'Server error';
    console.error(errMsg);

    return Observable.throw(new Error(errMsg));
  }

  /**
   * Build a post request to the given URL with the given data (serialized as JSON).
   *
   * The request is sent with a content type of 'application/json'.
   *
   * @param url
   * @param data
   * @returns {Observable<Response>}
   */
  private postAsJson(url: string, data: any) {
    let body = JSON.stringify(data);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(url, body, options);
  }

}
