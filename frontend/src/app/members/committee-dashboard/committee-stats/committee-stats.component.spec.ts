import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CommitteeStatsComponent} from './committee-stats.component';

describe('CommitteeStatsComponent', () => {

  let component: CommitteeStatsComponent;
  let fixture: ComponentFixture<CommitteeStatsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CommitteeStatsComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommitteeStatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

});
