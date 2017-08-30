import {Headers, Http, RequestOptions, Response} from '@angular/http';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/catch';

import {JwtService} from '../login/jwt.service';

/**
 * Basic methods shared across services.
 */
export class BaseService {

  http: Http;
  jwtService: JwtService;
  appVersion: string;

  constructor(http: Http, jwtService: JwtService, appVersion: string) {
    this.http = http;
    this.jwtService = jwtService;
    this.appVersion = appVersion;
  }

  /**
   * Handle a generic error encountered when performing an AJAX request.
   */
  protected handleError = <T>(error: any, caught: Observable<T>): Observable<T> => {
    if (error && error.status === 401) {
      this.jwtService.setJwt('', false);
    }

    throw caught;
  };

  /**
   * Build a post request to the given URL with the given data (serialized as JSON).
   *
   * The request is sent with a content type of 'application/json'.
   *
   * @param url
   * @param data
   * @returns {Observable<Response>}
   */
  protected post(url: string, data: any): Observable<Response> {
    const body = JSON.stringify(data);

    const response = this.http.post(url, body, this.makeRequestOptions());
    return response.catch(this.handleError);
  }

  /**
   * Build a PUT request to the given URL with the given data (serialized as JSON).
   *
   * The request is sent with a content type of 'application/json'.
   *
   * @param url
   * @param data
   * @returns {Observable<Response>}
   */
  protected put(url: string, data: any) {
    const body = JSON.stringify(data);

    const response = this.http.put(url, body, this.makeRequestOptions());
    return response.catch(this.handleError);
  }

  /**
   * Send a GET request.
   *
   * @param url
   * @returns {Observable<Response>}
   */
  protected get(url: string) {
    const response = this.http.get(url, this.makeRequestOptions());
    return response.catch(this.handleError);
  }

  private makeRequestOptions(): RequestOptions {
    const headers = new Headers({
      'Content-Type': 'application/json',
      'X-App-Version': this.appVersion,
      'X-JWT': this.jwtService.getJwt()
    });

    return new RequestOptions({headers});
  }

}
