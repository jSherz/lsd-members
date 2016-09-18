import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { PageRoutes } from './pages/page.routes';

export const routes: Routes = [
  ...PageRoutes,
  { path: 'admin', loadChildren: 'app/admin/admin.module#AdminModule' }
];

export const routing: ModuleWithProviders = RouterModule.forRoot(routes);
