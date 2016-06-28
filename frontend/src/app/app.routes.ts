import { provideRouter, RouterConfig } from '@angular/router';

import { SignupComponent } from './signup/signup.component';
import { SignupAltComponent } from './signup/signup-alt.component';

export const routes: RouterConfig = [
  { path: '', redirectTo: '/sign-up', terminal: true },
  { path: 'sign-up/alt', component: SignupAltComponent },
  { path: 'sign-up', component: SignupComponent }
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes)
];
