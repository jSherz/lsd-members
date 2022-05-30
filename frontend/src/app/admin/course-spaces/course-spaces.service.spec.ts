/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, async, TestBed } from "@angular/core/testing";
import { HttpClientModule } from "@angular/common/http";

import {
  CourseSpaceService,
  CourseSpaceServiceImpl
} from "./course-spaces.service";
import { API_KEY, StubApiKeyService } from "../utils/api-key.service.stub";
import { ApiKeyService } from "../utils/api-key.service";

describe("Service: CourseSpaces", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        { provide: API_KEY, useValue: "12345" },
        { provide: ApiKeyService, useClass: StubApiKeyService },
        { provide: CourseSpaceService, useClass: CourseSpaceServiceImpl }
      ]
    });
  });

  it("should ...", async(
    inject([CourseSpaceService], (service: CourseSpaceService) => {
      expect(service).toBeTruthy();
    })
  ));
});
