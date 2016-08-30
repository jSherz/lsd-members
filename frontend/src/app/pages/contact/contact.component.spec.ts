/* tslint:disable:no-unused-variable */

import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { addProviders, async, inject } from '@angular/core/testing';
import { ContactComponent } from './contact.component';

describe('Component: Contact', () => {
  it('should create an instance', () => {
    let component = new ContactComponent();
    expect(component).toBeTruthy();
  });
});
