/* tslint:disable:no-unused-variable */

import { Component } from "@angular/core";
import {
  inject,
  async,
  TestBed,
  ComponentFixture
} from "@angular/core/testing";
import { NavigationStart, Event, Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";

import { AppComponent } from "../../app.component";
import { BaseComponent } from "./base.component";
import { PageNavComponent } from "./page-nav.component";

describe("Component: Base", () => {
  let fixture: ComponentFixture<BaseComponent>;
  let component: BaseComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AppComponent, BaseComponent, PageNavComponent],
      imports: [RouterTestingModule]
    });

    fixture = TestBed.createComponent(BaseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should collapse the menu when the route changes", async(
    inject([Router], (router: Router) => {
      component.menuCollapsed = false;

      expect(component.menuCollapsed).toBeFalsy();

      (<any>router).events.next(new NavigationStart(123, "test-page"));

      expect(component.menuCollapsed).toBeTruthy();
    })
  ));
});
