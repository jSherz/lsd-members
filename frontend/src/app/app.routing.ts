import { Routes, RouterModule } from '@angular/router';

import { PageRoutes } from './pages/page.routes';

export const routes: Routes = [
  ...PageRoutes,
  // TODO: See if this can be used without "System is not defined" client errors
  // { path: 'admin', loadChildren: `app/admin/admin.module#AdminModule` }
];

export const routing = RouterModule.forRoot(routes);
