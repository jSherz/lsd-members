/* tslint:disable:no-unused-variable */

import {
  beforeEach, beforeEachProviders,
  describe, xdescribe,
  expect, it, xit,
  async, inject
} from '@angular/core/testing';
import { Title } from '@angular/platform-browser';
import { AppComponent } from './app.component';

beforeEachProviders(() => [AppComponent]);

describe('App: Luskydive', () => {
  it('should create the app', inject([AppComponent], (app: AppComponent) => {
    expect(app).toBeTruthy();
  }));

  it('appends the website title when setting the document title', () => {
    let mockedTitleService = new Title();
    let component = new AppComponent();

    component.setTitle('Test');

    expect(mockedTitleService.getTitle()).toEqual('Test - Leeds University Skydivers');
  });

  it('reverts to the website title when a blank itle is given', () => {
    let mockedTitleService = new Title();
    let component = new AppComponent();

    component.setTitle('Test');
    expect(mockedTitleService.getTitle()).toEqual('Test - Leeds University Skydivers');

    component.setTitle('');
    expect(mockedTitleService.getTitle()).toEqual('Leeds University Skydivers');
  });

  it('reverts to the website title when an undefined itle is given', () => {
    let mockedTitleService = new Title();
    let component = new AppComponent();

    component.setTitle('Test');
    expect(mockedTitleService.getTitle()).toEqual('Test - Leeds University Skydivers');

    component.setTitle(undefined);
    expect(mockedTitleService.getTitle()).toEqual('Leeds University Skydivers');
  });
});
