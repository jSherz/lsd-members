/* tslint:disable:no-unused-variable */

import {TestBed, async} from '@angular/core/testing';
import {FormBuilder} from '@angular/forms';
import {Router} from '@angular/router';

import {MassTextComponent} from './mass-text.component';
import {StubMassTextService} from './mass-text.service.stub';


describe('Component: MassText', () => {

  function mockComp(): MassTextComponent {
    let keys = [];
    for (let key in Router.prototype) {
      if (Router.prototype.hasOwnProperty(key)) {
        keys.push(key);
      }
    }

    let builder = new FormBuilder();
    let router = jasmine.createSpyObj('MockRouter', keys);
    let service = new StubMassTextService();

    return new MassTextComponent(builder, router, service);
  }

  it('should create an instance', async(() => {
    let component = mockComp();

    expect(component).toBeTruthy();
  }));

});
