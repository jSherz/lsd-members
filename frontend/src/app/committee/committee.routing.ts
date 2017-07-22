import {ModuleWithProviders} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

import {DashboardComponent} from './dashboard';
import {CanActivateCommittee} from './utils';

export const committeeRoutes: Routes = [
  {
    path: '',
    children: [
      {path: '', redirectTo: 'dashboard'},
      {path: 'dashboard', component: DashboardComponent, canActivate: [CanActivateCommittee]}
    ]
  }
];

export const committeeRouting: ModuleWithProviders = RouterModule.forChild(committeeRoutes);
