/* tslint:disable:no-unused-variable */

import {TestBed, async} from '@angular/core/testing';
import {FormBuilder} from '@angular/forms';
import {Router} from '@angular/router';

import {MassTextComponent} from './mass-text.component';
import {StubMassTextService} from './mass-text.service.stub';


describe('Component: MassText', () => {

  function mockComp(): MassTextComponent {
    const keys = [];
    for (const key in Router.prototype) {
      if (Router.prototype.hasOwnProperty(key)) {
        keys.push(key);
      }
    }

    const builder = new FormBuilder();
    const router = jasmine.createSpyObj('MockRouter', keys);
    const service = new StubMassTextService();

    const component = new MassTextComponent(builder, router, service);
    component.ngOnInit();

    return component;
  }

  it('should create an instance', async(() => {
    const component = mockComp();

    expect(component).toBeTruthy();
  }));

  it('should calculate the correct message', async(() => {
    const component = mockComp();

    expect(component.preview).toEqual('Hello, Mary - Reply \'NOFUN\' to stop these messages');
  }));

  it('should update the preview message when triggered', async(() => {
    const component = mockComp();

    component.ctrlTemplate.setValue('Hello {{ name }}. How are you today, {{ name }}?');
    component.updateTemplate();

    expect(component.preview).toEqual('Hello Mary. How are you today, Mary? - Reply \'NOFUN\' to stop these messages');
  }));

  it('should calculate the correct number of characters remaining', async(() => {
    const component = mockComp();

    const message = 'Hello {{ name }}. Or is it {{ name }}?';
    const messageNoPlaceholderLength = 18;
    const nameLength = 24;
    const optOutLength = 39;

    component.ctrlTemplate.setValue(message);
    component.updateTemplate();

    expect(component.numCharsTotal).toEqual(480);
    expect(component.numCharsUsed).toEqual(messageNoPlaceholderLength + nameLength + nameLength + optOutLength);
  }));

});
