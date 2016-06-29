import { RouterConfig } from '@angular/router';

import { SignupBaseComponent } from './signup-base.component';
import { SignupComponent }     from './signup.component';
import { SignupAltComponent }  from './signup-alt.component';

export const SignupRoutes: RouterConfig = [
  {
    path: 'sign-up',
    component: SignupBaseComponent,
    children: [
      { path: '', component: SignupComponent },
      { path: 'alt', component: SignupAltComponent }
    ]
  }
];
