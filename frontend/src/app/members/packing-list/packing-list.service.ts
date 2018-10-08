import {Injectable} from '@angular/core';
import {Http, Headers, Response} from '@angular/http';
import {Observable} from 'rxjs';

import {JwtService} from '../login/jwt.service';
import {PackingListItems} from './model';
import {environment} from '../../../environments/environment';
import { map } from 'rxjs/operators';

export abstract class PackingListService {

  abstract getPackingList(): Observable<PackingListItems>

  abstract putPackingList(packingListItems: PackingListItems): Observable<Response>

}

@Injectable()
export class PackingListServiceImpl extends PackingListService {

  static ApiUrl = environment.apiUrl + '/api/v1/packing-list';

  constructor(private http: Http, private jwtService: JwtService) {
    super();
  }

  getPackingList(): Observable<PackingListItems> {
    const headers = new Headers({'Authorization': 'Bearer ' + this.jwtService.getJwt()});

    return this.http.get(PackingListServiceImpl.ApiUrl, {headers})
      .pipe(map(r => r.json() as PackingListItems));
  }

  putPackingList(packingListItems: PackingListItems): Observable<Response> {
    const headers = new Headers({'Authorization': 'Bearer ' + this.jwtService.getJwt()});

    return this.http.put(PackingListServiceImpl.ApiUrl, packingListItems, {headers});
  }

}
