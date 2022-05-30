import {
  HttpHeaders,
  HttpClient,
  HttpClientModule,
  HttpResponse,
  HttpContext,
  HttpParams
} from "@angular/common/http";
import { Observable, throwError as observableThrowError, of } from "rxjs";
import { BaseService } from "./base.service";
import { async, inject, TestBed, waitForAsync } from "@angular/core/testing";
import { JwtService } from "../login";
import { StubJwtService } from "../login/jwt.service.stub";
import { APP_VERSION } from "../../app.module";

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
 * Create a dummy service using the base service.
 */
class MyService extends BaseService {
  constructor(http: HttpClient, jwtService: JwtService, appVersion: string) {
    super(http, jwtService, appVersion);
  }

  get<T>(url: string): Observable<T> {
    return super.get<T>(url);
  }

  post<T>(url: string, body: any): Observable<T> {
    return super.post<T>(url, body);
  }

  put<T>(url: string, body: any): Observable<T> {
    return super.put<T>(url, body);
  }
}

describe("Members: base service", () => {
  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientModule],
        providers: [
          {
            provide: JwtService,
            useValue: new StubJwtService("842374872874", true)
          },
          { provide: APP_VERSION, useValue: "my-app-version-123" }
        ]
      }).compileComponents();
    })
  );

  it("sends a GET request with the correct options", inject(
    [HttpClient, JwtService],
    (http: HttpClient, jwtService: JwtService) => {
      const appVersion = Math.random().toString();

      const service = new MyService(http, jwtService, appVersion);
      const dummyResponse = of(new HttpResponse());

      const httpSpy = spyOn(http, "get").and.returnValue(dummyResponse);
      const jwtSpy = spyOn(jwtService, "getJwt").and.callThrough();

      service.get("https://www.example.com/new-pics");

      expect(httpSpy.calls.count()).toEqual(1);
      const httpArgs = httpSpy.calls.argsFor(0);
      expect(httpArgs[0]).toEqual("https://www.example.com/new-pics");

      expect(jwtSpy).toHaveBeenCalled();

      const options: RequestOptions = httpArgs[1];
      const headers: HttpHeaders = options.headers as HttpHeaders;

      expect(headers.get("Content-Type")).toEqual("application/json");
      expect(headers.get("Authorization")).toEqual("Bearer 842374872874");
      expect(headers.get("X-App-Version")).toEqual(appVersion);
    }
  ));

  it("sends a POST request with the correct JSON body & options", inject(
    [HttpClient, JwtService],
    (http: HttpClient, jwtService: JwtService) => {
      const appVersion = Math.random().toString();

      const service = new MyService(http, jwtService, appVersion);
      const dummyResponse = of(new HttpResponse());

      const httpSpy = spyOn(http, "post").and.returnValue(dummyResponse);
      const jwtSpy = spyOn(jwtService, "getJwt").and.callThrough();
      const body = {
        myKey: "myValue"
      };

      service.post(
        "https://magical-api-machine.example.org/v1/rest2soap",
        body
      );

      expect(httpSpy.calls.count()).toEqual(1);
      const httpArgs = httpSpy.calls.argsFor(0);
      expect(httpArgs[0]).toEqual(
        "https://magical-api-machine.example.org/v1/rest2soap"
      );
      expect(httpArgs[1]).toEqual('{"myKey":"myValue"}');

      expect(jwtSpy).toHaveBeenCalled();

      const options: RequestOptions = httpArgs[2];
      const headers: HttpHeaders = options.headers as HttpHeaders;

      expect(headers.get("Content-Type")).toEqual("application/json");
      expect(headers.get("Authorization")).toEqual("Bearer 842374872874");
      expect(headers.get("X-App-Version")).toEqual(appVersion);
    }
  ));

  it("sends a PUT request with the correct JSON body & options", inject(
    [HttpClient, JwtService],
    (http: HttpClient, jwtService: JwtService) => {
      const appVersion = Math.random().toString();

      const service = new MyService(http, jwtService, appVersion);
      const dummyResponse = of(new HttpResponse());

      const httpSpy = spyOn(http, "put").and.returnValue(dummyResponse);
      const jwtSpy = spyOn(jwtService, "getJwt").and.callThrough();
      const body = {
        another: true
      };

      service.put("https://hobbit-factory.example.org/api/hobbits", body);

      expect(httpSpy.calls.count()).toEqual(1);
      const httpArgs = httpSpy.calls.argsFor(0);
      expect(httpArgs[0]).toEqual(
        "https://hobbit-factory.example.org/api/hobbits"
      );
      expect(httpArgs[1]).toEqual('{"another":true}');

      expect(jwtSpy).toHaveBeenCalled();

      const options: RequestOptions = httpArgs[2];
      const headers: HttpHeaders = options.headers as HttpHeaders;

      expect(headers.get("Content-Type")).toEqual("application/json");
      expect(headers.get("Authorization")).toEqual("Bearer 842374872874");
      expect(headers.get("X-App-Version")).toEqual(appVersion);
    }
  ));

  it("clears the JWT if the request fails with a 401 error code (GET)", async(
    inject(
      [HttpClient, JwtService],
      (http: HttpClient, jwtService: JwtService) => {
        const service = new MyService(http, jwtService, "appv51");

        const unauthResponse = new HttpResponse({
          status: 401,
          body: "Computer says no",
          url: "https://computer-says-no.example.com"
        });
        const dummyResponse = observableThrowError(unauthResponse);

        const httpSpy = spyOn(http, "get").and.returnValue(dummyResponse);
        const jwtSpy = spyOn(jwtService, "setJwt").and.callThrough();

        service.get("https://some-url.example.com").subscribe(
          () => null,
          () => {
            expect(httpSpy.calls.count()).toEqual(1);
            expect(jwtSpy).toHaveBeenCalledWith("", false);
          }
        );
      }
    )
  ));

  it("clears the JWT if the request fails with a 401 error code (POST)", async(
    inject(
      [HttpClient, JwtService],
      (http: HttpClient, jwtService: JwtService) => {
        const service = new MyService(http, jwtService, "v131313");

        const unauthResponse = new HttpResponse({
          status: 401,
          body: "This is not the response you're looking for",
          url: "https://have-another-mint.example.com"
        });
        const dummyResponse = observableThrowError(() => unauthResponse);

        const httpSpy = spyOn(http, "post").and.returnValue(dummyResponse);
        const jwtSpy = spyOn(jwtService, "setJwt").and.callThrough();

        service
          .post("https://another-bucket-of-s3.example.com", { foo: "barred" })
          .subscribe(
            () => null,
            () => {
              expect(httpSpy.calls.count()).toEqual(1);
              expect(jwtSpy).toHaveBeenCalledWith("", false);
            }
          );
      }
    )
  ));

  it("clears the JWT if the request fails with a 401 error code (PUT)", async(
    inject(
      [HttpClient, JwtService],
      (http: HttpClient, jwtService: JwtService) => {
        const service = new MyService(http, jwtService, "8.819191.1r2");

        const unauthResponse = new HttpResponse({
          status: 401,
          body: "ARE U 4 REAL?",
          url: "https://no-no-no-no-no-auth.example.com"
        });
        const dummyResponse = observableThrowError(unauthResponse);

        const httpSpy = spyOn(http, "put").and.returnValue(dummyResponse);
        const jwtSpy = spyOn(jwtService, "setJwt").and.callThrough();

        service
          .put("https://magical-cloud-factory.example.com", { answer: 42 })
          .subscribe(
            () => null,
            () => {
              expect(httpSpy.calls.count()).toEqual(1);
              expect(jwtSpy).toHaveBeenCalledWith("", false);
            }
          );
      }
    )
  ));
});
