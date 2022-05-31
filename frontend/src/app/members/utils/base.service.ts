import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";

import { JwtService } from "../login/jwt.service";
import {
  HttpClient,
  HttpContext,
  HttpHeaders,
  HttpParams,
  HttpResponse
} from "@angular/common/http";

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
  http: HttpClient;
  jwtService: JwtService;
  appVersion: string;

  constructor(http: HttpClient, jwtService: JwtService, appVersion: string) {
    this.http = http;
    this.jwtService = jwtService;
    this.appVersion = appVersion;
  }

  /**
   * Handle a generic error encountered when performing an AJAX request.
   */
  protected handleError = <T>(
    error: any,
    caught: Observable<T>
  ): Observable<T> => {
    if (error && error.status === 401) {
      this.jwtService.setJwt("", false);
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
   * @returns {Observable<HttpResponse>}
   */
  protected post<T>(url: string, data: any): Observable<T> {
    const body = JSON.stringify(data);

    const response = this.http.post<T>(url, body, this.makeRequestOptions());
    return response.pipe(catchError(this.handleError));
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

    const response = this.http.put<T>(url, body, this.makeRequestOptions());
    return response.pipe(catchError(this.handleError));
  }

  /**
   * Send a GET request.
   *
   * @param url
   * @returns {Observable<Response>}
   */
  protected get<T>(url: string): Observable<T> {
    const response = this.http.get<T>(url, this.makeRequestOptions());
    return response.pipe(catchError(this.handleError));
  }

  private makeRequestOptions(): RequestOptions {
    const headers = new HttpHeaders({
      "Content-Type": "application/json",
      "X-App-Version": this.appVersion,
      Authorization: "Bearer " + this.jwtService.getJwt()
    });

    return { headers };
  }
}
