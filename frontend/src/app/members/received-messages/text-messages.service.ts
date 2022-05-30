import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { JwtService } from "../login/jwt.service";
import { Observable } from "rxjs";
import { TextMessage } from "./text-message";
import { environment } from "../../../environments/environment";
import { map } from "rxjs/operators";

export abstract class TextMessagesService {
  abstract getReceivedMessages(): Observable<TextMessage[]>;
}

@Injectable()
export class TextMessagesServiceImpl extends TextMessagesService {
  private getReceivedUrl: string =
    environment.apiUrl + "/api/v1/text-messages/received";

  constructor(private http: HttpClient, private jwtService: JwtService) {
    super();
  }

  getReceivedMessages(): Observable<TextMessage[]> {
    const headers = new HttpHeaders({
      Authorization: "Bearer " + this.jwtService.getJwt()
    });

    return this.http
      .get(this.getReceivedUrl, { headers })
      .pipe(map(r => (r as unknown) as TextMessage[]));
  }
}
