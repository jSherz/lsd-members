import {async, ComponentFixture, inject, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';

import {DashboardComponent} from './dashboard.component';
import {HeaderComponent} from '../../members/header';
import {
  DashboardStatsComponent,
  DashboardStatsService
} from './stats';

import {StubDashboardStatsService} from './stats/stub-committee-stats.service'

describe('DashboardComponent', () => {

  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        HeaderComponent,
        DashboardStatsComponent,
        DashboardComponent
      ],
      providers: [
        {provide: DashboardStatsService, useValue: new StubDashboardStatsService}
      ]
  }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

});
