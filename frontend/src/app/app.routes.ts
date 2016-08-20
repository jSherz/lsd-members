import { Routes, RouterModule } from '@angular/router';

import { SignupRoutes } from './signup/signup.routes';
import { AdminRoutes  } from './admin/admin.routes';

export const routes: Routes = [
  { path: '', redirectTo: '/sign-up', terminal: true },
  ...SignupRoutes,
  ...AdminRoutes
];

export const routing = RouterModule.forRoot(routes);
