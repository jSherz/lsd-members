import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {Http} from '@angular/http';
import {JwtService} from '../../../members/login/jwt.service';
import {NumReceivedMessages} from './num-received-messages';
import {Inject, Injectable} from '@angular/core';
import {BaseService} from '../../../members/utils/base.service';
import {APP_VERSION} from '../../../app.module';
import { map } from 'rxjs/operators';

export abstract class DashboardStatsService extends BaseService {

  constructor(http: Http, jwtService: JwtService, appVersion: string) {
    super(http, jwtService, appVersion);
  }

  abstract getNumReceivedMessages(): Observable<number>

}

@Injectable()
export class DashboardStatsServiceImpl extends DashboardStatsService {

  private numReceivedUrl = environment.apiUrl + '/api/v1/text-messages/num-received';

  constructor(http: Http, jwtService: JwtService, @Inject(APP_VERSION) appVersion: string) {
    super(http, jwtService, appVersion);
  }

  getNumReceivedMessages(): Observable<number> {
    return this.get(this.numReceivedUrl)
      .pipe(
        map(r => r.json() as NumReceivedMessages),
        map(r => r.numReceived)
      );
  }

}
