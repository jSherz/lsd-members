import {
  throwError as observableThrowError,
  Observable,
  ObservableInput
} from "rxjs";
import {
  HttpResponse,
  HttpHeaders,
  HttpClient,
  HttpContext,
  HttpParams
} from "@angular/common/http";

import { ApiKeyService } from "./";

interface RequestOptions {
  headers?:
    | HttpHeaders
    | {
        [header: string]: string | string[];
      };
  context?: HttpContext;
  observe?: "body";
  params?:
    | HttpParams
    | {
        [param: string]:
          | string
          | number
          | boolean
          | ReadonlyArray<string | number | boolean>;
      };
  reportProgress?: boolean;
  responseType?: "json";
  withCredentials?: boolean;
}

/**
 * Basic methods shared across services.
 */
export class BaseService {
  private http: HttpClient;
  private apiKeyService: ApiKeyService;

  constructor(http: HttpClient, apiKeyService: ApiKeyService) {
    this.http = http;
    this.apiKeyService = apiKeyService;
  }

  /**
   * Handle a generic error encountered when performing an AJAX request.
   */
  protected handleError<T>() {
    // We use a closure here to ensure that the reference to this.apiKeyService isn't lost when this error handler is used
    const apiKeyService = this.apiKeyService;

    const innerHandler = (err: any, caught: Observable<T>): never => {
      const errMsg = err.message
        ? err.message
        : err.status
        ? `${err.status} - ${err.statusText}`
        : "Server error";
      console.error(errMsg);

      if (err.status && err.status === 401) {
        apiKeyService.setKey("");
      }

      throw new Error(errMsg);
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
  protected post<T>(url: string, data: any): Observable<T> {
    const body = JSON.stringify(data);
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Api-Key": this.apiKeyService.getKey()
    });

    return this.http.post<T>(url, body, { headers });
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
  protected put<T>(url: string, data: any): Observable<T> {
    const body = JSON.stringify(data);
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      "Api-Key": this.apiKeyService.getKey()
    });

    return this.http.put<T>(url, body, { headers });
  }

  /**
   * Send a GET request.
   *
   * @param url
   * @returns {Observable<Response>}
   */
  protected get<T>(url: string): Observable<T> {
    const headers = new HttpHeaders({ "Api-Key": this.apiKeyService.getKey() });

    return this.http.get<T>(url, { headers });
  }
}
