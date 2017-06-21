import {Observable} from 'rxjs/Observable';
import {environment} from '../../../../environments/environment';
import {Http, Headers} from '@angular/http';
import {JwtService} from '../../login/jwt.service';
import {NumReceivedMessages} from './num-received-messages';
import {Injectable} from '@angular/core';

export abstract class CommitteeStatsService {

  abstract getNumReceivedMessages(): Observable<number>

}

@Injectable()
export class CommitteeStatsServiceImpl extends CommitteeStatsService {

  private numReceivedUrl = environment.apiUrl + '/api/v1/text-messages/num-received';

  constructor(private http: Http, private jwtService: JwtService) {
    super();
  }

  getNumReceivedMessages(): Observable<number> {
    const headers = new Headers({'X-JWT': this.jwtService.getJwt()});

    return this.http.get(this.numReceivedUrl, {headers})
      .map(r => r.json() as NumReceivedMessages)
      .map(r => r.numReceived);
  }

}
