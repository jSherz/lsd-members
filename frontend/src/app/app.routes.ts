import { Routes, RouterModule } from '@angular/router';

import { SignupRoutes } from './signup/signup.routes';
import { AdminRoutes  } from './admin/admin.routes';
import { PageRoutes   } from './pages/page.routes';

export const routes: Routes = [
  ...PageRoutes,
  ...SignupRoutes,
  ...AdminRoutes
];

export const routing = RouterModule.forRoot(routes);
