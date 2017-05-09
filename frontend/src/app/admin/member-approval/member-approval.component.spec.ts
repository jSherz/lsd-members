import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MemberApprovalComponent} from './member-approval.component';

describe('MemberApprovalComponent', () => {
  let component: MemberApprovalComponent;
  let fixture: ComponentFixture<MemberApprovalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MemberApprovalComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemberApprovalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
