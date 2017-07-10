import {async, ComponentFixture, inject, TestBed} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';

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
