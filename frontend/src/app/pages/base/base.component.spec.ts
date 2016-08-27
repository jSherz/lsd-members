/* tslint:disable:no-unused-variable */

import { By }           from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { addProviders, async, inject } from '@angular/core/testing';
import { BaseComponent } from './base.component';

describe('Component: Base', () => {
  it('should create an instance', () => {
    let component = new BaseComponent();
    expect(component).toBeTruthy();
  });
});
