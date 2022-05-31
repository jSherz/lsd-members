/* eslint-disable @typescript-eslint/no-unused-vars */

import { inject, TestBed, async } from "@angular/core/testing";
import { HttpClientModule } from "@angular/common/http";

import { CourseService, CourseServiceImpl } from "./course.service";
import { API_KEY, StubApiKeyService } from "../utils/api-key.service.stub";
import { ApiKeyService } from "../utils/api-key.service";

describe("Course Service", () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
      providers: [
        { provide: API_KEY, useValue: "12345" },
        { provide: ApiKeyService, useClass: StubApiKeyService },
        { provide: CourseService, useClass: CourseServiceImpl }
      ]
    });
  });

  it("should ...", async(
    inject([CourseService], (service: CourseService) => {
      expect(service).toBeTruthy();
    })
  ));
});
