import {ModuleWithProviders} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

import {PageRoutes} from './pages/page.routes';
import {NotFoundComponent} from './pages/not-found/not-found.component';

export const routes: Routes = [
  ...PageRoutes,
  {path: 'admin', loadChildren: 'app/admin/admin.module#AdminModule'},
  {path: 'members', loadChildren: 'app/members/members.module#MembersModule'},
  { path: '**', component: NotFoundComponent }
];

export const routing: ModuleWithProviders = RouterModule.forRoot(routes);
