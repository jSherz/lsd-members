import {Observable} from 'rxjs/Observable';
import {environment} from '../../../../environments/environment';
import {Http} from '@angular/http';
import {JwtService} from '../../login/jwt.service';
import {NumReceivedMessages} from './num-received-messages';
import {Inject, Injectable} from '@angular/core';
import {BaseService} from '../../utils/base.service';
import {APP_VERSION} from '../../../app.module';

export abstract class CommitteeStatsService extends BaseService {

  constructor(http: Http, jwtService: JwtService, appVersion: string) {
    super(http, jwtService, appVersion);
  }

  abstract getNumReceivedMessages(): Observable<number>

}

@Injectable()
export class CommitteeStatsServiceImpl extends CommitteeStatsService {

  private numReceivedUrl = environment.apiUrl + '/api/v1/text-messages/num-received';

  constructor(http: Http, jwtService: JwtService, @Inject(APP_VERSION) appVersion: string) {
    super(http, jwtService, appVersion);
  }

  getNumReceivedMessages(): Observable<number> {
    return this.get(this.numReceivedUrl)
      .map(r => r.json() as NumReceivedMessages)
      .map(r => r.numReceived);
  }

}
