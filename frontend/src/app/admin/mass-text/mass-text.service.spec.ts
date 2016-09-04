/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { MassTextService } from './mass-text.service';

describe('Service: MassText', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [MassTextService]
    });
  });

  it('should ...', inject([MassTextService], (service: MassTextService) => {
    expect(service).toBeTruthy();
  }));
});
