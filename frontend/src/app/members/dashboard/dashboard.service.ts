import {Inject, Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';

import {BasicInfo} from './basic-info';
import {environment} from '../../../environments/environment';
import {BaseService} from '../utils/base.service';
import {JwtService} from '../login/jwt.service';
import {APP_VERSION} from 'app/app.module';

export abstract class DashboardService extends BaseService {

  constructor(http: Http, jwtService: JwtService, appVersion: string) {
    super(http, jwtService, appVersion);
  }

  abstract getBasicInfo(): Observable<BasicInfo>

}

@Injectable()
export class DashboardServiceImpl extends DashboardService {

  private basicInfoUrl = environment.apiUrl + '/api/v1/me';

  constructor(http: Http, jwtService: JwtService, @Inject(APP_VERSION) appVersion: string) {
    super(http, jwtService, appVersion);
  }

  getBasicInfo(): Observable<BasicInfo> {
    if (this.jwtService.isAuthenticated()) {
      return this.get(this.basicInfoUrl)
        .map(r => r.json() as BasicInfo);
    } else {
      return Observable.of(null);
    }
  }

}
