/* tslint:disable:no-unused-variable */

import { inject           } from '@angular/core/testing';
import { TestBed          } from '@angular/core/testing/test_bed';

import { CommitteeService } from './committee.service';

describe('Service: Committee', () => {

  it('should ...', inject([CommitteeService], (service: CommitteeService) => {
    expect(service).toBeTruthy();
  }));

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CommitteeService]
    });
  });

});
