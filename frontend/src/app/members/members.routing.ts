import {ModuleWithProviders} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

import {MembersComponent} from './members.component';
import {LoginComponent} from './login';
import {DashboardComponent} from './dashboard';

export const membersRoutes: Routes = [
  {
    path: '',
    component: MembersComponent,
    children: [
      {path: '', component: LoginComponent},
      {path: 'dashboard', component: DashboardComponent}
    ]
  }
];

export const membersRouting: ModuleWithProviders = RouterModule.forChild(membersRoutes);
