import { Injectable } from "@angular/core";
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot
} from "@angular/router";

import { JwtService } from "../login/jwt.service";

/**
 * Checks to see if a member is logged in before they try and access a route.
 *
 * If they're not logged in, they'll be redirected to the members social login page.
 */
@Injectable()
export class CanActivateMembers implements CanActivate {
  constructor(private jwtService: JwtService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (!this.jwtService.isAuthenticated()) {
      this.router.navigate(["/members"]);
    }

    return this.jwtService.isAuthenticated();
  }
}
