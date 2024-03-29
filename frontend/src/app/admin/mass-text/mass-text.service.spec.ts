/* eslint-disable @typescript-eslint/no-unused-vars */

import { TestBed, async, inject } from "@angular/core/testing";
import { HttpClientModule } from "@angular/common/http";

import { MassTextService, MassTextServiceImpl } from "./mass-text.service";
import { ApiKeyService } from "../utils/api-key.service";
import { API_KEY, StubApiKeyService } from "../utils/api-key.service.stub";

describe("Service: MassText", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        { provide: API_KEY, useValue: "487a2930-8e6a-41a5-bcc0-b7fd7f2421e4" },
        { provide: MassTextService, useClass: MassTextServiceImpl },
        { provide: ApiKeyService, useClass: StubApiKeyService }
      ]
    });
  });

  it("should be creatable", inject(
    [MassTextService],
    (service: MassTextService) => {
      expect(service).toBeTruthy();
    }
  ));
});
