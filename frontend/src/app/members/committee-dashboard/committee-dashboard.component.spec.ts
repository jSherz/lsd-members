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
  ChildrenOutletContexts
} from '@angular/router';

import {CommitteeDashboardComponent} from './committee-dashboard.component';
import {HeaderComponent} from '../header';
import {CommitteeStatsComponent} from './committee-stats';
import {CommitteeStatsService} from './committee-stats/committee-stats.service';
import {StubCommitteeStatsService} from './committee-stats/stub-committee-stats.service';

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
        CommitteeStatsComponent,
        CommitteeDashboardComponent
      ],
      providers: [
        {provide: UrlHandlingStrategy, useValue: {}},
        {
          provide: Router,
          useFactory: setupTestingRouter,
          deps: [UrlSerializer, ChildrenOutletContexts, Location, NgModuleFactoryLoader, Compiler, Injector, ROUTES, UrlHandlingStrategy]
        },
        {provide: CommitteeStatsService, useClass: StubCommitteeStatsService}
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
