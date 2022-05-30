import { waitForAsync, ComponentFixture, TestBed } from "@angular/core/testing";

import { ReceivedMessagesComponent } from "./received-messages.component";
import { TextMessagesService } from "./text-messages.service";
import { of } from "rxjs";
import { HeaderComponent } from "../header/header.component";
import { UrlHandlingStrategy } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";

const dummyService = {
  getReceivedMessages: () => {
    return of([]);
  }
};

describe("ReceivedMessagesComponent", () => {
  let component: ReceivedMessagesComponent;
  let fixture: ComponentFixture<ReceivedMessagesComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          RouterTestingModule.withRoutes([
            { path: "", component: ReceivedMessagesComponent }
          ])
        ],
        declarations: [HeaderComponent, ReceivedMessagesComponent],
        providers: [
          { provide: UrlHandlingStrategy, useValue: {} },
          { provide: TextMessagesService, useValue: dummyService }
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceivedMessagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should be created", () => {
    expect(component).toBeTruthy();
  });
});
