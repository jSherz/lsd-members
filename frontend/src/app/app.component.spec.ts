/* tslint:disable:no-unused-variable */

import {inject, async} from '@angular/core/testing';
import {TestBed} from '@angular/core/testing/test_bed';

import {AppComponent} from './app.component';

describe('Component: App', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AppComponent]
    });
  });

  it('should create the app', async(inject([AppComponent], (app: AppComponent) => {
    expect(app).toBeTruthy();
  })));

});
