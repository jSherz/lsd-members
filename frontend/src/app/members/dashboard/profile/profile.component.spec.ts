import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ProfileComponent} from './profile.component';
import {BasicInfo} from '../basic-info';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ProfileComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should display a name if one is provided', () => {
    component.basicInfo = new BasicInfo();
    component.basicInfo.firstName = 'Billy';

    expect(component.hasName()).toBeTruthy();
  });

  it('should display no name if one isn\'t provided', () => {
    component.basicInfo = new BasicInfo();
    component.basicInfo.firstName = '';
    expect(component.hasName()).toBeFalsy();

    component.basicInfo = null;
    expect(component.hasName()).toBeFalsy();
  });

});
