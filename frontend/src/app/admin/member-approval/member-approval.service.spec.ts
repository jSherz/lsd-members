import { TestBed, inject } from "@angular/core/testing";

import {
  MemberApprovalService,
  MemberApprovalServiceImpl
} from "./member-approval.service";
import { HttpClientModule } from "@angular/common/http";
import { ApiKeyService } from "../utils/api-key.service";
import { API_KEY, StubApiKeyService } from "../utils/api-key.service.stub";

describe("MemberApprovalService", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        { provide: API_KEY, useValue: "foobarred" },
        { provide: ApiKeyService, useClass: StubApiKeyService },
        { provide: MemberApprovalService, useClass: MemberApprovalServiceImpl }
      ]
    });
  });

  it("should ...", inject(
    [MemberApprovalService],
    (service: MemberApprovalService) => {
      expect(service).toBeTruthy();
    }
  ));
});
