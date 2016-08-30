import { Routes } from '@angular/router';

import { AdminBaseComponent } from './admin-base.component';
import {
  CourseCalendarComponent,
  CourseViewComponent,
  CourseAddComponent
} from './courses';
import { LoginComponent } from './login/login.component';
import { CanActivateAdmin } from './can-activate-admin';


export const AdminRoutes: Routes = [
  {
    path: 'admin',
    component: AdminBaseComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'courses/calendar/:year/:month', component: CourseCalendarComponent, canActivate: [CanActivateAdmin] },
      { path: 'courses/calendar', component: CourseCalendarComponent, canActivate: [CanActivateAdmin] },
      { path: 'courses/:uuid', component: CourseViewComponent, canActivate: [CanActivateAdmin] },
      { path: 'courses/add/:year/:month/:day', component: CourseAddComponent, canActivate: [CanActivateAdmin] }
    ]
  }
];
