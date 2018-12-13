/* tslint:disable:no-unused-variable */

import { async, TestBed, inject } from "@angular/core/testing";
import { Http, HttpModule } from "@angular/http";

import { MemberServiceImpl } from "./member.service";
import { ApiKeyService } from "../utils/api-key.service";
import { StubApiKeyService, API_KEY } from "../utils/api-key.service.stub";

describe("Service: Member", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpModule],
      providers: [
        { provide: API_KEY, useValue: "12345" },
        { provide: ApiKeyService, useClass: StubApiKeyService }
      ]
    });
  });

  it("should ...", async(
    inject([Http, ApiKeyService], (http, apiKeyService) => {
      const service = new MemberServiceImpl(http, apiKeyService);
      expect(service).toBeTruthy();
    })
  ));
});
