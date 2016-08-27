import { Routes } from '@angular/router';

import {
  BaseComponent,
  HomeComponent,
  AboutComponent,
  ContactComponent,
  CommitteeComponent
} from './index';

export const PageRoutes: Routes = [
  {
    path: '',
    component: BaseComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'about-the-club', component: AboutComponent },
      { path: 'contact', component: ContactComponent },
      { path: 'committee', component: CommitteeComponent }
    ]
  }
];
