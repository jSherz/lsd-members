import { Routes } from '@angular/router';

import {
  BaseComponent,
  HomeComponent
} from './index';

export const PageRoutes: Routes = [
  {
    path: '',
    component: BaseComponent,
    children: [
      { path: '', component: HomeComponent }
    ]
  }
];
