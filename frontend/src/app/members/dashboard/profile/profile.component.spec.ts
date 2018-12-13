import { async, ComponentFixture, TestBed } from "@angular/core/testing";

import { ProfileComponent } from "./profile.component";
import { BasicInfo } from "../basic-info";

describe("ProfileComponent", () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProfileComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should display a name if one is provided", () => {
    component.basicInfo = new BasicInfo(
      "b9424a23-fc0e-4b02-9897-21ba09868c55",
      "Billy",
      null,
      null
    );

    expect(component.hasName()).toBeTruthy();
  });

  it("should display no name if one isn't provided", () => {
    component.basicInfo = new BasicInfo(
      "63f4df43-5da8-4a36-8d7b-5ff984c3d7ce",
      "",
      null,
      null
    );
    expect(component.hasName()).toBeFalsy();

    component.basicInfo = null;
    expect(component.hasName()).toBeFalsy();
  });
});
