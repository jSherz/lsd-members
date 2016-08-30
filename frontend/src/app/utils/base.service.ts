import {
  Response,
  Headers,
  RequestOptions,
  Http
} from '@angular/http';
import { Observable      } from 'rxjs';
import { ErrorObservable } from 'rxjs/observable/ErrorObservable';

import { ApiKeyService } from './index';

/**
 * Basic methods shared across services.
 */
export class BaseService {

  http: Http;
  apiKeyService: ApiKeyService;

  constructor(http: Http, apiKeyService: ApiKeyService) {
    this.http = http;
    this.apiKeyService = apiKeyService;
  }

  /**
   * Extract the JSON body of a response.
   *
   * @param res
   * @returns
   */
  protected extractJson<T>(res: Response): T {
    let body = res.json();
    return body || [];
  }

  /**
   * Handle a generic error encountered when performing an AJAX request.
   */
  protected handleError<R, T>() {
    // We use a closure here to ensure that the reference to this.apiKeyService isn't lost when this error handler is used
    let apiKeyService = this.apiKeyService;

    let innerHandler = <R, T>(err: any, caught: Observable<T>): ErrorObservable => {
      let errMsg = (err.message) ? err.message : err.status ? `${err.status} - ${err.statusText}` : 'Server error';
      console.error(errMsg);

      if (err.status && err.status === 401) {
        apiKeyService.setKey('');
      }

      return Observable.throw(new Error(errMsg));
    };

    return innerHandler;
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
  protected post(url: string, data: any) {
    let body = JSON.stringify(data);
    let headers = new Headers({ 'Content-Type': 'application/json', 'Api-Key': this.apiKeyService.getKey() });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(url, body, options);
  }

  /**
   * Send a GET request.
   *
   * @param url
   * @returns {Observable<Response>}
   */
  protected get(url: string) {
    let headers = new Headers({ 'Api-Key': this.apiKeyService.getKey() });
    let options = new RequestOptions({ headers: headers });

    return this.http.get(url, options);
  }

}
