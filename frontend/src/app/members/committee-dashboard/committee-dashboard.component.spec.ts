import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {CommitteeDashboardComponent} from './committee-dashboard.component';

describe('CommitteeDashboardComponent', () => {

  let component: CommitteeDashboardComponent;
  let fixture: ComponentFixture<CommitteeDashboardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CommitteeDashboardComponent]
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
