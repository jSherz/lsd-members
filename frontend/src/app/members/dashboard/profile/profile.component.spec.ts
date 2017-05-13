import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ProfileComponent} from './profile.component';

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
    component.name = 'Billy';

    expect(component.hasName()).toBeTruthy();
  });

  it('should display no name if one isn\'t provided', () => {
    component.name = '';
    expect(component.hasName()).toBeFalsy();

    component.name = null;
    expect(component.hasName()).toBeFalsy();
  });

});
