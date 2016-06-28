import { provideRouter, RouterConfig } from '@angular/router';

import { SignupRoutes } from './signup/signup.routes';
import { AdminRoutes } from './admin/admin.routes';

export const routes: RouterConfig = [
  { path: '', redirectTo: '/sign-up', terminal: true },
  ...SignupRoutes,
  ...AdminRoutes
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes)
];
