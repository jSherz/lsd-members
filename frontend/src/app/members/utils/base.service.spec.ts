import {Headers, Http, HttpModule, RequestOptions, Response, ResponseOptions} from '@angular/http';
import {Observable, throwError as observableThrowError, of} from 'rxjs';
import {BaseService} from './base.service';
import {async, inject, TestBed} from '@angular/core/testing';
import {JwtService} from '../login';
import {StubJwtService} from '../login/jwt.service.stub';
import {APP_VERSION} from '../../app.module';

/**
 * Create a dummy service using the base service.
 */
class MyService extends BaseService {
  constructor(http: Http, jwtService: JwtService, appVersion: string) {
    super(http, jwtService, appVersion);
  }

  get(url: string): Observable<Response> {
    return super.get(url);
  }

  post(url: string, body: any): Observable<Response> {
    return super.post(url, body);
  }

  put(url: string, body: any): Observable<Response> {
    return super.put(url, body);
  }
}

describe('Members: base service', () => {

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        {provide: JwtService, useValue: new StubJwtService('842374872874', true)},
        {provide: APP_VERSION, useValue: 'my-app-version-123'}
      ]
    }).compileComponents();
  }));

  it('sends a GET request with the correct options', inject([Http, JwtService], (http: Http, jwtService: JwtService) => {
    const appVersion = Math.random().toString();

    const service = new MyService(http, jwtService, appVersion);
    const dummyResponse = of(new Response(new ResponseOptions()));

    const httpSpy = spyOn(http, 'get').and.returnValue(dummyResponse);
    const jwtSpy = spyOn(jwtService, 'getJwt').and.callThrough();

    service.get('https://www.example.com/new-pics');

    expect(httpSpy.calls.count()).toEqual(1);
    const httpArgs = httpSpy.calls.argsFor(0);
    expect(httpArgs[0]).toEqual('https://www.example.com/new-pics');

    expect(jwtSpy).toHaveBeenCalled();

    const options: RequestOptions = httpArgs[1];
    const headers: Headers = options.headers;

    expect(headers.get('Content-Type')).toEqual('application/json');
    expect(headers.get('Authorization')).toEqual('Bearer 842374872874');
    expect(headers.get('X-App-Version')).toEqual(appVersion);
  }));

  it('sends a POST request with the correct JSON body & options', inject([Http, JwtService], (http: Http, jwtService: JwtService) => {
    const appVersion = Math.random().toString();

    const service = new MyService(http, jwtService, appVersion);
    const dummyResponse = of(new Response(new ResponseOptions()));

    const httpSpy = spyOn(http, 'post').and.returnValue(dummyResponse);
    const jwtSpy = spyOn(jwtService, 'getJwt').and.callThrough();
    const body = {
      myKey: 'myValue'
    };

    service.post('https://magical-api-machine.example.org/v1/rest2soap', body);

    expect(httpSpy.calls.count()).toEqual(1);
    const httpArgs = httpSpy.calls.argsFor(0);
    expect(httpArgs[0]).toEqual('https://magical-api-machine.example.org/v1/rest2soap');
    expect(httpArgs[1]).toEqual('{"myKey":"myValue"}');

    expect(jwtSpy).toHaveBeenCalled();

    const options: RequestOptions = httpArgs[2];
    const headers: Headers = options.headers;

    expect(headers.get('Content-Type')).toEqual('application/json');
    expect(headers.get('Authorization')).toEqual('Bearer 842374872874');
    expect(headers.get('X-App-Version')).toEqual(appVersion);
  }));

  it('sends a PUT request with the correct JSON body & options', inject([Http, JwtService], (http: Http, jwtService: JwtService) => {
    const appVersion = Math.random().toString();

    const service = new MyService(http, jwtService, appVersion);
    const dummyResponse = of(new Response(new ResponseOptions()));

    const httpSpy = spyOn(http, 'put').and.returnValue(dummyResponse);
    const jwtSpy = spyOn(jwtService, 'getJwt').and.callThrough();
    const body = {
      another: true
    };

    service.put('https://hobbit-factory.example.org/api/hobbits', body);

    expect(httpSpy.calls.count()).toEqual(1);
    const httpArgs = httpSpy.calls.argsFor(0);
    expect(httpArgs[0]).toEqual('https://hobbit-factory.example.org/api/hobbits');
    expect(httpArgs[1]).toEqual('{"another":true}');

    expect(jwtSpy).toHaveBeenCalled();

    const options: RequestOptions = httpArgs[2];
    const headers: Headers = options.headers;

    expect(headers.get('Content-Type')).toEqual('application/json');
    expect(headers.get('Authorization')).toEqual('Bearer 842374872874');
    expect(headers.get('X-App-Version')).toEqual(appVersion);
  }));

  it('clears the JWT if the request fails with a 401 error code (GET)', async(inject([Http, JwtService],
    (http: Http, jwtService: JwtService) => {
      const service = new MyService(http, jwtService, 'appv51');

      const unauthResponse = new Response(new ResponseOptions({
        status: 401,
        body: 'Computer says no',
        url: 'https://computer-says-no.example.com'
      }));
      const dummyResponse = observableThrowError(unauthResponse);

      const httpSpy = spyOn(http, 'get').and.returnValue(dummyResponse);
      const jwtSpy = spyOn(jwtService, 'setJwt').and.callThrough();

      service.get('https://some-url.example.com')
        .subscribe(() => null, () => {
          expect(httpSpy.calls.count()).toEqual(1);
          expect(jwtSpy).toHaveBeenCalledWith('', false);
        });
    })));

  it('clears the JWT if the request fails with a 401 error code (POST)', async(inject([Http, JwtService],
    (http: Http, jwtService: JwtService) => {
      const service = new MyService(http, jwtService, 'v131313');

      const unauthResponse = new Response(new ResponseOptions({
        status: 401,
        body: 'This is not the response you\'re looking for',
        url: 'https://have-another-mint.example.com'
      }));
      const dummyResponse = observableThrowError(unauthResponse);

      const httpSpy = spyOn(http, 'post').and.returnValue(dummyResponse);
      const jwtSpy = spyOn(jwtService, 'setJwt').and.callThrough();

      service.post('https://another-bucket-of-s3.example.com', {foo: 'barred'})
        .subscribe(() => null, () => {
          expect(httpSpy.calls.count()).toEqual(1);
          expect(jwtSpy).toHaveBeenCalledWith('', false);
        });
    })));

  it('clears the JWT if the request fails with a 401 error code (PUT)', async(inject([Http, JwtService],
    (http: Http, jwtService: JwtService) => {
      const service = new MyService(http, jwtService, '8.819191.1r2');

      const unauthResponse = new Response(new ResponseOptions({
        status: 401,
        body: 'ARE U 4 REAL?',
        url: 'https://no-no-no-no-no-auth.example.com'
      }));
      const dummyResponse = observableThrowError(unauthResponse);

      const httpSpy = spyOn(http, 'put').and.returnValue(dummyResponse);
      const jwtSpy = spyOn(jwtService, 'setJwt').and.callThrough();

      service.put('https://magical-cloud-factory.example.com', {answer: 42})
        .subscribe(() => null, () => {
          expect(httpSpy.calls.count()).toEqual(1);
          expect(jwtSpy).toHaveBeenCalledWith('', false);
        });
    })));

});
