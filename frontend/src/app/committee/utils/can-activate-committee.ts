import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';

import {JwtService} from 'app/members/login/jwt.service';

/**
 * Checks to see if a member is logged in as a committee member before they try and access a route.
 *
 * If they're not logged in, they'll be redirected to the members social login page.
 */
@Injectable()
export class CanActivateCommittee implements CanActivate {

  constructor(private jwtService: JwtService, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (!this.jwtService.isAuthenticated() || !this.jwtService.isCommitteeMember()) {
      this.router.navigate(['/members']);

      return false;
    } else {
      return true;
    }
  }

}
