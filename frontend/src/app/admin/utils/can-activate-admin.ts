import {CanActivate, RouterStateSnapshot, ActivatedRouteSnapshot, Router} from '@angular/router';
import {Injectable} from '@angular/core';

import {ApiKeyService} from './api-key.service';


@Injectable()
export class CanActivateAdmin implements CanActivate {

  constructor(private apiKeyService: ApiKeyService, private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (!this.apiKeyService.isAuthenticated()) {
      this.router.navigate(['/admin', 'login']);
    }

    return this.apiKeyService.isAuthenticated();
  }

}
