import {ModuleWithProviders} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

import {MembersComponent} from './members.component';
import {LoginComponent} from './login';

export const membersRoutes: Routes = [
  {
    path: '',
    component: MembersComponent,
    children: [
      {path: 'login', component: LoginComponent}
      // {path: 'dashboard', component: DashboardComponent, canActivate: [CanActivateAdmin]}
    ]
  }
];

export const membersRouting: ModuleWithProviders = RouterModule.forChild(membersRoutes);
