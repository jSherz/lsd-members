import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {
  CanActivateAdmin,
  AdminBaseComponent,
  LoginComponent,
  CourseCalendarComponent,
  CourseViewComponent,
  CourseAddComponent,
  MassTextComponent,
  SignupComponent,
  SignupAltComponent,
  ThankYouComponent
} from './';
import {SignupBaseComponent} from "./signup/signup-base.component";


export const adminRoutes: Routes = [
  {
    path: 'admin',
    component: AdminBaseComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'courses/calendar/:year/:month', component: CourseCalendarComponent, canActivate: [CanActivateAdmin] },
      { path: 'courses/calendar', component: CourseCalendarComponent, canActivate: [CanActivateAdmin] },
      { path: 'courses/:uuid', component: CourseViewComponent, canActivate: [CanActivateAdmin] },
      { path: 'courses/add/:year/:month/:day', component: CourseAddComponent, canActivate: [CanActivateAdmin] },
      { path: 'mass-text', component: MassTextComponent, canActivate: [CanActivateAdmin] }
    ]
  },
  {
    path: 'sign-up',
    component: SignupBaseComponent,
    children: [
      { path: '', component: SignupComponent },
      { path: 'alt', component: SignupAltComponent },
      { path: 'thank-you', component: ThankYouComponent }
    ]
  }
];

export const adminRouting: ModuleWithProviders = RouterModule.forChild(adminRoutes);
