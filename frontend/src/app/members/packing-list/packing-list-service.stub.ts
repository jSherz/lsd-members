import { Observable } from "rxjs";
import { HttpResponse } from "@angular/common/http";

import { PackingListService } from "./packing-list.service";
import { PackingListItems } from "./model";

export class StubPackingListService extends PackingListService {
  constructor(
    private item: Observable<PackingListItems>,
    private putResult: Observable<HttpResponse<unknown>>
  ) {
    super();
  }

  getPackingList(): Observable<PackingListItems> {
    return this.item;
  }

  putPackingList(packingListItems: PackingListItems): Observable<HttpResponse<unknown>> {
    return this.putResult;
  }
}
