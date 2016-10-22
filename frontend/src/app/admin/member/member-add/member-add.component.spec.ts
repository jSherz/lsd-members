/* tslint:disable:no-unused-variable */

import {TestBed, async} from '@angular/core/testing';
import {FormBuilder} from '@angular/forms';
import {Router} from '@angular/router';

import {MemberAddComponent} from './member-add.component';
import {StubMemberService} from '../member.service.stub';

describe('Component: MemberAdd', () => {

  function mockComp(): MemberAddComponent {
    let keys = [];
    for (let key in Router.prototype) {
      if (Router.prototype.hasOwnProperty(key)) {
        keys.push(key);
      }
    }

    let builder = new FormBuilder();
    let router = jasmine.createSpyObj('MockRouter', keys);
    let service = new StubMemberService();

    return new MemberAddComponent(builder, service, router);
  }

  it('should create an instance', async(() => {
    let component = mockComp();

    expect(component).toBeTruthy();
  }));

});
