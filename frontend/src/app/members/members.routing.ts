import {ModuleWithProviders} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

import {MembersComponent} from './members.component';
import {LoginComponent} from './login';
import {DashboardComponent} from './dashboard';
import {NotApprovedComponent} from './not-approved';

export const membersRoutes: Routes = [
  {
    path: '',
    component: MembersComponent,
    children: [
      {path: '', component: LoginComponent},
      {path: 'dashboard', component: DashboardComponent},
      {path: 'not-approved', component: NotApprovedComponent}
    ]
  }
];

export const membersRouting: ModuleWithProviders = RouterModule.forChild(membersRoutes);
