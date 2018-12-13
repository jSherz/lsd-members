import { Observable } from "rxjs";
import { Response } from "@angular/http";

import { PackingListService } from "./packing-list.service";
import { PackingListItems } from "./model";

export class StubPackingListService extends PackingListService {
  constructor(
    private item: Observable<PackingListItems>,
    private putResult: Observable<Response>
  ) {
    super();
  }

  getPackingList(): Observable<PackingListItems> {
    return this.item;
  }

  putPackingList(packingListItems: PackingListItems): Observable<Response> {
    return this.putResult;
  }
}
