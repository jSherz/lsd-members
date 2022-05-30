import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpResponse } from "@angular/common/http";
import { Observable } from "rxjs";

import { JwtService } from "../login/jwt.service";
import { PackingListItems } from "./model";
import { environment } from "../../../environments/environment";
import { map } from "rxjs/operators";

export abstract class PackingListService {
  abstract getPackingList(): Observable<PackingListItems>;

  abstract putPackingList(
    packingListItems: PackingListItems
  ): Observable<HttpResponse<unknown>>;
}

@Injectable()
export class PackingListServiceImpl extends PackingListService {
  static ApiUrl = environment.apiUrl + "/api/v1/packing-list";

  constructor(private http: HttpClient, private jwtService: JwtService) {
    super();
  }

  getPackingList(): Observable<PackingListItems> {
    const headers = new HttpHeaders({
      Authorization: "Bearer " + this.jwtService.getJwt()
    });

    return this.http
      .get(PackingListServiceImpl.ApiUrl, { headers })
      .pipe(map(r => r as PackingListItems));
  }

  putPackingList(
    packingListItems: PackingListItems
  ): Observable<HttpResponse<Object>> {
    const headers = new HttpHeaders({
      Authorization: "Bearer " + this.jwtService.getJwt()
    });

    return this.http.put<HttpResponse<Object>>(
      PackingListServiceImpl.ApiUrl,
      packingListItems,
      {
        headers
      }
    );
  }
}
