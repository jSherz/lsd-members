import { TestBed, inject, async } from "@angular/core/testing";
import { HttpClientModule } from "@angular/common/http";

import {
  PackingListService,
  PackingListServiceImpl
} from "./packing-list.service";
import { JwtService } from "../login/jwt.service";
import { StubJwtService } from "../login/jwt.service.stub";

describe("PackingListService", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        { provide: PackingListService, useClass: PackingListServiceImpl },
        { provide: JwtService, useValue: new StubJwtService("asdf", false) }
      ]
    });
  });

  it("should be created", async(
    inject([PackingListService], (packingListService: PackingListService) => {})
  ));
});
