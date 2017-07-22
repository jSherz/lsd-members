import {async} from '@angular/core/testing';

import {CanActivateCommittee} from './';

describe('CanActivateCommittee', () => {

  it('redirects to the login page when the member is not logged in', async(() => {
    const jwtService: any = {
      isAuthenticated: () => false,
      isCommitteeMember: () => false
    };
    const router: any = {
      navigate: () => null
    };

    const canActivate = new CanActivateCommittee(jwtService, router);

    spyOn(router, 'navigate');

    expect(canActivate.canActivate(null, null)).toBeFalsy();
    expect(router.navigate).toHaveBeenCalledWith(['/members']);
  }));

  it('redirects to the login page when the member is logged in but not a committee member', async(() => {
    const jwtService: any = {
      isAuthenticated: () => true,
      isCommitteeMember: () => false
    };
    const router: any = {
      navigate: () => null
    };

    const canActivate = new CanActivateCommittee(jwtService, router);

    spyOn(router, 'navigate');

    expect(canActivate.canActivate(null, null)).toBeFalsy();
    expect(router.navigate).toHaveBeenCalledWith(['/members']);
  }));

  it('returns true when the member is logged in', async(() => {
    const jwtService: any = {
      isAuthenticated: () => true,
      isCommitteeMember: () => true
    };
    const router: any = {
      navigate: () => null
    };

    const canActivate = new CanActivateCommittee(jwtService, router);

    expect(canActivate.canActivate(null, null)).toBeTruthy();
  }));

});
