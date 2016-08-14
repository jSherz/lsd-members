import { RouterConfig } from '@angular/router';

import { AdminBaseComponent      } from './admin-base.component';
import { CourseCalendarComponent } from './courses/course-calendar.component';
import { CourseViewComponent     } from './courses/course-view';

export const AdminRoutes: RouterConfig = [
  {
    path: 'admin',
    component: AdminBaseComponent,
    children: [
      { path: 'courses/calendar/:year/:month', component: CourseCalendarComponent },
      { path: 'courses/calendar', component: CourseCalendarComponent },
      { path: 'courses/:uuid', component: CourseViewComponent }
    ]
  }
];
