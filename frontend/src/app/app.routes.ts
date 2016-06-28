import { provideRouter, RouterConfig } from '@angular/router';

import { SignupComponent } from './signup/signup.component';

export const routes: RouterConfig = [
  { path: '', component: SignupComponent }
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes)
];
