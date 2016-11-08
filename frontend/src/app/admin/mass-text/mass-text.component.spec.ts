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

    let component = new MassTextComponent(builder, router, service);
    component.ngOnInit();

    return component;
  }

  it('should create an instance', async(() => {
    let component = mockComp();

    expect(component).toBeTruthy();
  }));

  it('should calculate the correct message', async(() => {
    let component = mockComp();

    expect(component.preview).toEqual('Hello, Mary - Reply \'NOFUN\' to stop these messages');
  }));

  it('should update the preview message when triggered', async(() => {
    let component = mockComp();

    component.ctrlTemplate.setValue('Hello {{ name }}. How are you today, {{ name }}?');
    component.updateTemplate();

    expect(component.preview).toEqual('Hello Mary. How are you today, Mary? - Reply \'NOFUN\' to stop these messages');
  }));

  it('should calculate the correct number of characters remaining', async(() => {
    let component = mockComp();

    let message = 'Hello {{ name }}. Or is it {{ name }}?';
    let messageNoPlaceholderLength = 18;
    let nameLength = 24;
    let optOutLength = 39;

    component.ctrlTemplate.setValue(message);
    component.updateTemplate();

    expect(component.numCharsTotal).toEqual(480);
    expect(component.numCharsUsed).toEqual(messageNoPlaceholderLength + nameLength + nameLength + optOutLength);
  }));

});
