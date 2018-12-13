import { Compiler, Injector, NgModuleFactoryLoader } from "@angular/core";
import {
  async,
  ComponentFixture,
  inject,
  TestBed
} from "@angular/core/testing";
import {
  RouterTestingModule,
  setupTestingRouter
} from "@angular/router/testing";
import { Location } from "@angular/common";

import {
  Router,
  Event,
  UrlHandlingStrategy,
  ROUTES,
  UrlSerializer,
  ChildrenOutletContexts
} from "@angular/router";

import { HeaderComponent } from "./header.component";

describe("HeaderComponent", () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [HeaderComponent],
      providers: [
        { provide: UrlHandlingStrategy, useValue: {} },
        {
          provide: Router,
          useFactory: setupTestingRouter,
          deps: [
            UrlSerializer,
            ChildrenOutletContexts,
            Location,
            NgModuleFactoryLoader,
            Compiler,
            Injector,
            ROUTES,
            UrlHandlingStrategy
          ]
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should be created", () => {
    expect(component).toBeTruthy();
  });
});
