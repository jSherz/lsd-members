import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ReceivedMessagesComponent} from './received-messages.component';
import {TextMessagesService} from './text-messages.service';
import {Observable} from 'rxjs/Observable';

describe('ReceivedMessagesComponent', () => {
  let component: ReceivedMessagesComponent;
  let fixture: ComponentFixture<ReceivedMessagesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ReceivedMessagesComponent
      ],
      providers: [
        {
          provide: TextMessagesService, useValue: {
          getReceivedMessages: () => {
            return Observable.of([]);
          }
        }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceivedMessagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

});
