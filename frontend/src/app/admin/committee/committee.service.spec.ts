/* tslint:disable:no-unused-variable */

import {inject, async} from '@angular/core/testing';
import {TestBed} from '@angular/core/testing/test_bed';

import {CommitteeService, CommitteeServiceImpl} from './committee.service';

describe('Service: Committee', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {provide: CommitteeService, useClass: CommitteeServiceImpl}
      ]
    });
  });

  it('should ...', async(inject([CommitteeService], (service: CommitteeService) => {
    expect(service).toBeTruthy();
  })));

});
