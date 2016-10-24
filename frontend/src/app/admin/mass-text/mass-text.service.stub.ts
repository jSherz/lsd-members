import {Injectable} from '@angular/core';
import * as moment from 'moment';
import {Observable} from 'rxjs';

import {MassTextSendResponse} from './mass-text-send-response';
import {MassTextService} from './mass-text.service';


@Injectable()
export class StubMassTextService extends MassTextService {

  constructor() {
    super(undefined, undefined);
  }

  send(startDate: moment.Moment, endDate: moment.Moment, template: string,
       expectedRendered: string): Observable<MassTextSendResponse> {
    return Observable.of(new MassTextSendResponse(true, undefined, '12345'));
  }

}
