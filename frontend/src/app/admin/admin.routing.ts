import {ModuleWithProviders} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

import {
  CanActivateAdmin,
  AdminBaseComponent,
  LoginComponent,
  CourseCalendarComponent,
  CourseViewComponent,
  CourseAddComponent,
  MassTextComponent,
  SignupBaseComponent,
  SignupComponent,
  SignupAltComponent,
  ThankYouComponent,
  MemberViewComponent
} from './';


export const adminRoutes: Routes = [
  {
    path: '',
    component: AdminBaseComponent,
    children: [
      {path: 'login', component: LoginComponent},
      {path: 'courses/calendar/:year/:month', component: CourseCalendarComponent, canActivate: [CanActivateAdmin]},
      {path: 'courses/calendar', component: CourseCalendarComponent, canActivate: [CanActivateAdmin]},
      {path: 'courses/add', component: CourseAddComponent, canActivate: [CanActivateAdmin]},
      {path: 'courses/:uuid', component: CourseViewComponent, canActivate: [CanActivateAdmin]},
      {path: 'mass-text', component: MassTextComponent, canActivate: [CanActivateAdmin]},
      {path: 'members/:uuid', component: MemberViewComponent, canActivate: [CanActivateAdmin]}
    ]
  },
  {
    path: 'sign-up',
    component: SignupBaseComponent,
    children: [
      {path: '', component: SignupComponent},
      {path: 'alt', component: SignupAltComponent},
      {path: 'thank-you', component: ThankYouComponent}
    ]
  }
];

export const adminRouting: ModuleWithProviders = RouterModule.forChild(adminRoutes);
