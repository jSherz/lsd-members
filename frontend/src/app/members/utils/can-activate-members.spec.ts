import {async} from '@angular/core/testing';

import {CanActivateMembers} from './can-activate-members';

describe('CanActivateMembers', () => {

  it('redirects to the login page when the member is not logged in', async(() => {
    const jwtService: any = {
      isAuthenticated: () => null
    };
    const router: any = {
      navigate: () => null
    };

    const canActivate = new CanActivateMembers(jwtService, router);

    spyOn(jwtService, 'isAuthenticated').and.returnValue(false);
    spyOn(router, 'navigate');

    expect(canActivate.canActivate(null, null)).toBeFalsy();
    expect(router.navigate).toHaveBeenCalledWith(['/members']);
  }));

  it('returns true when the member is logged in', async(() => {
    const jwtService: any = {
      isAuthenticated: () => null
    };
    const router: any = {
      navigate: () => null
    };

    const canActivate = new CanActivateMembers(jwtService, router);

    spyOn(jwtService, 'isAuthenticated').and.returnValue(true);

    expect(canActivate.canActivate(null, null)).toBeTruthy();
  }));

});
