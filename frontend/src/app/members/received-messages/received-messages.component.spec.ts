import {async, ComponentFixture, inject, TestBed} from '@angular/core/testing';

import {ReceivedMessagesComponent} from './received-messages.component';
import {TextMessagesService} from './text-messages.service';
import {Observable} from 'rxjs/Observable';
import {HeaderComponent} from '../header/header.component';
import {
  Router, UrlHandlingStrategy, UrlSerializer, ChildrenOutletContexts, ROUTES, Route
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
      imports: [
        RouterTestingModule.withRoutes([
          {path: '', component: ReceivedMessagesComponent}
        ])
      ],
      declarations: [
        HeaderComponent,
        ReceivedMessagesComponent
      ],
      providers: [
        {provide: UrlHandlingStrategy, useValue: {}},
        {provide: TextMessagesService, useValue: dummyService}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceivedMessagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', async(() => {
    expect(component).toBeTruthy();
  }));

});
