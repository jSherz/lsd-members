import {Injectable} from '@angular/core';
import {Http, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import 'rxjs/add/operator/map';

import {JwtService} from '../login/jwt.service';
import {BasicInfo} from './basic-info';
import {environment} from '../../../environments/environment';

export abstract class DashboardService {

  abstract getBasicInfo(): Observable<BasicInfo>

}

@Injectable()
export class DashboardServiceImpl extends DashboardService {

  private basicInfoUrl = environment.apiUrl + '/api/v1/me';

  constructor(private http: Http, private jwtService: JwtService) {
    super();
  }

  getBasicInfo(): Observable<BasicInfo> {
    if (this.jwtService.isAuthenticated()) {
      const headers = new Headers({'X-JWT': this.jwtService.getJwt()});

      return this.http.get(this.basicInfoUrl, {headers})
        .map(r => r.json() as BasicInfo);
    } else {
      return Observable.of(null);
    }
  }

}
