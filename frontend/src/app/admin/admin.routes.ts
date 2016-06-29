import { RouterConfig } from '@angular/router';

import { AdminBaseComponent } from './admin-base.component';
import { CourseCalendarComponent } from './courses/course-calendar.component';

export const AdminRoutes: RouterConfig = [
  {
    path: 'admin',
    component: AdminBaseComponent,
    children: [
      { path: 'courses/calendar', component: CourseCalendarComponent }
      // { path: 'courses/:id', component: SignupAltComponent }
    ]
  }
];
