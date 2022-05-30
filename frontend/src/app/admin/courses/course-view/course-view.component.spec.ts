/* eslint-disable @typescript-eslint/no-unused-vars */

import { ActivatedRoute, Params, UrlSegment } from "@angular/router";
import { async } from "@angular/core/testing";
import { Observable, of, Subject } from "rxjs";

import { CourseViewComponent } from "./course-view.component";
import { StubCourseService } from "../course.service.stub";
import { StubCourseSpaceService } from "../../course-spaces/course-spaces.service.stub";
import { stubExampleSpaces } from "../model/stub-example-spaces";
import { CourseWithOrganisers, CourseSpaceWithMember } from "../model";

describe("Component: CourseView", () => {
  it("should show a loading indicator while waiting for the course info & spaces", async(() => {
    const setup = mockComp(StubCourseService.getDontCompleteUuid);

    expect(setup.component.showThrobber).toBeTruthy();

    setup.getCourseSubject.next(StubCourseService.getOkCourse);

    expect(setup.component.showThrobber).toBeTruthy();

    setup.courseSpacesSubject.next(stubExampleSpaces);

    expect(setup.component.showThrobber).toBeFalsy();
  }));

  it("should show a loading indicator while waiting for the course info & spaces (spaces returns first)", async(() => {
    const setup = mockComp(StubCourseService.getDontCompleteUuid);

    expect(setup.component.showThrobber).toBeTruthy();

    setup.courseSpacesSubject.next(stubExampleSpaces);

    expect(setup.component.showThrobber).toBeTruthy();

    setup.getCourseSubject.next(StubCourseService.getOkCourse);

    expect(setup.component.showThrobber).toBeFalsy();
  }));

  it("should hide the throbber if the get spaces request fails", async(() => {
    const setup = mockComp(StubCourseService.getDontCompleteUuid);

    expect(setup.component.showThrobber).toBeTruthy();

    setup.courseSpacesSubject.error("Simulate course request failing");

    expect(setup.component.showThrobber).toBeFalsy();
    expect(setup.component.spaces).toEqual([]);
  }));

  it("should hide the throbber if the get course request fails", async(() => {
    const setup = mockComp(StubCourseService.getDontCompleteUuid);

    expect(setup.component.showThrobber).toBeTruthy();

    setup.getCourseSubject.error("Simulate course info request failing");

    expect(setup.component.showThrobber).toBeFalsy();
    expect(setup.component.course).toEqual(null);
  }));

  it("should show the correct course, picked up from the URL", async(() => {
    const setup = mockComp(StubCourseService.getOkUuid);

    expect(setup.component.course).toEqual(StubCourseService.getOkCourse);
  }));

  it("should show the correct spaces, picked up from the URL", async(() => {
    const setup = mockComp(StubCourseService.spacesOkUuid);

    expect(setup.component.spaces).toEqual(stubExampleSpaces);
  }));
});

class TestSetup {
  component: CourseViewComponent;
  getCourseSubject: Subject<CourseWithOrganisers>;
  courseSpacesSubject: Subject<CourseSpaceWithMember[]>;

  constructor(
    component: CourseViewComponent,
    getCourseSubject: Subject<CourseWithOrganisers>,
    courseSpacesSubject: Subject<CourseSpaceWithMember[]>
  ) {
    this.component = component;
    this.getCourseSubject = getCourseSubject;
    this.courseSpacesSubject = courseSpacesSubject;
  }
}

/**
 * Create a course view component with a stubbed course service.
 *
 * @param courseUuid Course to "display"
 * @returns {CourseViewComponent}
 */
function mockComp(courseUuid: string): TestSetup {
  const params = { uuid: courseUuid };
  const urlParts = [["courses", params]];

  const urls = [new UrlSegment("courses", { uuid: courseUuid })];

  const observableUrls = of(urls);
  const observableParams: Observable<Params> = of(params);

  const activatedRoute = new ActivatedRoute();
  activatedRoute.url = observableUrls;
  activatedRoute.params = observableParams;

  const service = new StubCourseService([]);
  const spaceService = new StubCourseSpaceService();

  const component = new CourseViewComponent(
    activatedRoute,
    service,
    spaceService
  );
  component.ngOnInit();

  return new TestSetup(
    component,
    service.getDontCompleteSubject,
    service.spacesDontCompleteSubject
  );
}
