import {TestBed, inject} from '@angular/core/testing';

import {MemberApprovalService, MemberApprovalServiceImpl} from './member-approval.service';
import {HttpModule} from '@angular/http';
import {ApiKeyService} from '../utils/api-key.service';
import {StubApiKeyService} from '../utils/api-key.service.stub';

describe('MemberApprovalService', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpModule
      ],
      providers: [
        {provide: ApiKeyService, useValue: new StubApiKeyService('foobarred')},
        {provide: MemberApprovalService, useClass: MemberApprovalServiceImpl}
      ]
    });
  });

  it('should ...', inject([MemberApprovalService], (service: MemberApprovalService) => {
    expect(service).toBeTruthy();
  }));

});
