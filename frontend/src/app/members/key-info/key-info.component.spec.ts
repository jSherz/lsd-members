import { async, ComponentFixture, TestBed } from "@angular/core/testing";

import { KeyInfoComponent } from "./key-info.component";

describe("KeyInfoComponent", () => {
  let component: KeyInfoComponent;
  let fixture: ComponentFixture<KeyInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [KeyInfoComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(KeyInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should be created", () => {
    expect(component).toBeTruthy();
  });
});
