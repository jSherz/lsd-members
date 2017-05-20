import {ModuleWithProviders} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

import {MembersComponent} from './members.component';
import {LoginComponent} from './login';
import {DashboardComponent} from './dashboard';
import {NotApprovedComponent} from './not-approved';
import {PerformLoginComponent} from './social-login';

export const membersRoutes: Routes = [
  {
    path: '',
    component: MembersComponent,
    children: [
      {path: '', component: LoginComponent},
      {path: 'dashboard', component: DashboardComponent},
      {path: 'not-approved', component: NotApprovedComponent},
      {path: 'perform-login', component: PerformLoginComponent}
    ]
  }
];

export const membersRouting: ModuleWithProviders = RouterModule.forChild(membersRoutes);
