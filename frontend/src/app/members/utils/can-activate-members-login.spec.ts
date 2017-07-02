import {CanActivateMembersLogin} from './can-activate-members-login';
import {async} from '@angular/core/testing';

describe('CanActivateMembersLogin', () => {

  it('redirects to the dashboard when the member is logged in', async(() => {
    const jwtService: any = {
      isAuthenticated: () => null
    };
    const router: any = {
      navigate: () => null
    };

    const canActivate = new CanActivateMembersLogin(jwtService, router);

    spyOn(jwtService, 'isAuthenticated').and.returnValue(true);
    spyOn(router, 'navigate');

    expect(canActivate.canActivate(null, null)).toBeFalsy();
    expect(router.navigate).toHaveBeenCalledWith(['/members', 'dashboard']);
  }));

  it('returns an Observable.of(true) when the member is not logged in', async(() => {
    const jwtService: any = {
      isAuthenticated: () => null
    };
    const router: any = {
      navigate: () => null
    };

    const canActivate = new CanActivateMembersLogin(jwtService, router);

    spyOn(jwtService, 'isAuthenticated').and.returnValue(false);

    expect(canActivate.canActivate(null, null)).toBeTruthy();
  }));

});
