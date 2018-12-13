import { async, ComponentFixture, TestBed } from "@angular/core/testing";

import { TheWeatherComponent } from "./the-weather.component";

describe("TheWeatherComponent", () => {
  let component: TheWeatherComponent;
  let fixture: ComponentFixture<TheWeatherComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TheWeatherComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TheWeatherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should be created", () => {
    expect(component).toBeTruthy();
  });
});
