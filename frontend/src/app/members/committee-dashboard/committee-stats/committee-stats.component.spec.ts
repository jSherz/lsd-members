import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CommitteeStatsComponent} from './committee-stats.component';
import {CommitteeStatsService} from './committee-stats.service';
import {StubJwtService} from '../../login/jwt.service.stub';
import {JwtService} from '../../login/jwt.service';
import {HttpModule} from '@angular/http';
import {StubCommitteeStatsService} from './stub-committee-stats.service';

describe('CommitteeStatsComponent', () => {

  let component: CommitteeStatsComponent;
  let fixture: ComponentFixture<CommitteeStatsComponent>;
  let service: StubCommitteeStatsService;

  const serviceFactory = () => {
    service = new StubCommitteeStatsService();
    return service;
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      declarations: [CommitteeStatsComponent],
      providers: [
        {provide: CommitteeStatsService, useFactory: serviceFactory},
        {provide: JwtService, useValue: new StubJwtService('asdf', true)}
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommitteeStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should show the returned number of received messages', () => {
    service.numReceived.next(13781);

    expect(component.numReceived).toEqual(13781);
  });

});
