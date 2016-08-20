import { Routes } from '@angular/router';

import { AdminBaseComponent } from './admin-base.component';
import {
  CourseCalendarComponent,
  CourseViewComponent,
  CourseAddComponent
} from './courses';

export const AdminRoutes: Routes = [
  {
    path: 'admin',
    component: AdminBaseComponent,
    children: [
      { path: 'courses/calendar/:year/:month', component: CourseCalendarComponent },
      { path: 'courses/calendar', component: CourseCalendarComponent },
      { path: 'courses/:uuid', component: CourseViewComponent },
      { path: 'courses/add/:year/:month/:day', component: CourseAddComponent }
    ]
  }
];
