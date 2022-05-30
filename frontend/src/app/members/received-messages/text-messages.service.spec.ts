import { TestBed, inject } from "@angular/core/testing";

import {
  TextMessagesService,
  TextMessagesServiceImpl
} from "./text-messages.service";
import { HttpClientModule } from "@angular/common/http";
import { JwtService } from "../login/jwt.service";
import { StubJwtService } from "../login/jwt.service.stub";

describe("TextMessagesService", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        { provide: TextMessagesService, useClass: TextMessagesServiceImpl },
        { provide: JwtService, useValue: new StubJwtService("asdf", true) }
      ]
    });
  });

  it("should be created", inject(
    [TextMessagesService],
    (service: TextMessagesService) => {
      expect(service).toBeTruthy();
    }
  ));
});
