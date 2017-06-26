import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ReceivedMessagesComponent} from './received-messages.component';
import {TextMessagesService} from './text-messages.service';
import {Observable} from 'rxjs/Observable';
import {HeaderComponent} from '../header/header.component';
import {
  Router, UrlHandlingStrategy, UrlSerializer, RouterOutletMap, ROUTES
} from '@angular/router';
import {RouterTestingModule, setupTestingRouter} from '@angular/router/testing';
import {Compiler, Injector, NgModuleFactoryLoader} from '@angular/core';
import {Location} from '@angular/common';

const dummyService = {
  getReceivedMessages: () => {
    return Observable.of([]);
  }
};

describe('ReceivedMessagesComponent', () => {
  let component: ReceivedMessagesComponent;
  let fixture: ComponentFixture<ReceivedMessagesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [
        HeaderComponent,
        ReceivedMessagesComponent
      ],
      providers: [
        {provide: UrlHandlingStrategy, useValue: {}},
        {
          provide: Router,
          useFactory: setupTestingRouter,
          deps: [UrlSerializer, RouterOutletMap, Location, NgModuleFactoryLoader, Compiler, Injector, ROUTES, UrlHandlingStrategy]
        },
        {provide: TextMessagesService, useValue: dummyService}
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
