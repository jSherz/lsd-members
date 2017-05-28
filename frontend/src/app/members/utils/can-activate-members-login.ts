import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {JwtService} from '../login/jwt.service';

/**
 * Checks to see if a member is logged in before the route to the login page is activated.
 *
 * If the member *is* logged in, they'll be redirected to the dashboard.
 */
@Injectable()
export class CanActivateMembersLogin implements CanActivate {

  constructor(private jwtService: JwtService, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    if (this.jwtService.isAuthenticated()) {
      this.router.navigate(['/members', 'dashboard']);
    }

    return Observable.of(this.jwtService.isAuthenticated());
  }

}
