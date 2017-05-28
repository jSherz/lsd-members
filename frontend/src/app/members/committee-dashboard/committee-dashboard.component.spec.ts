import {Compiler, Injector, NgModuleFactoryLoader} from '@angular/core';
import {async, ComponentFixture, inject, TestBed} from '@angular/core/testing';
import {RouterTestingModule, setupTestingRouter} from '@angular/router/testing';
import {Location} from '@angular/common';

import {
  Router,
  Event,
  UrlHandlingStrategy,
  ROUTES,
  UrlSerializer,
  RouterOutletMap
} from '@angular/router';

import {CommitteeDashboardComponent} from './committee-dashboard.component';
import {HeaderComponent} from '../header';

describe('CommitteeDashboardComponent', () => {

  let component: CommitteeDashboardComponent;
  let fixture: ComponentFixture<CommitteeDashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        HeaderComponent,
        CommitteeDashboardComponent
      ],
      providers: [
        {provide: UrlHandlingStrategy, useValue: {}},
        {
          provide: Router,
          useFactory: setupTestingRouter,
          deps: [UrlSerializer, RouterOutletMap, Location, NgModuleFactoryLoader, Compiler, Injector, ROUTES, UrlHandlingStrategy]
        },
      ]
  }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommitteeDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

});
