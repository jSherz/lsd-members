import { RouterConfig } from '@angular/router';

import {
  SignupComponent,
  SignupAltComponent,
  SignupBaseComponent,
  ThankYouComponent
} from './index';

export const SignupRoutes: RouterConfig = [
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
