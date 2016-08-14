/* tslint:disable:no-unused-variable */

import { addProviders, async, inject } from '@angular/core/testing';
import { CommitteeService } from './committee.service';

describe('Service: Committee', () => {
  beforeEach(() => {
    addProviders([CommitteeService]);
  });

  it('should ...',
    inject([CommitteeService],
      (service: CommitteeService) => {
        expect(service).toBeTruthy();
      }));
});
