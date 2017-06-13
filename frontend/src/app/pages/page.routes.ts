import { Routes } from '@angular/router';

import {
  BaseComponent,
  HomeComponent,
  AboutComponent,
  ContactComponent,
  CommitteeComponent,
  FaqComponent,
  PricesComponent,
  NotFoundComponent
} from './index';

export const PageRoutes: Routes = [
  {
    path: '',
    component: BaseComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'about-the-club', component: AboutComponent },
      { path: 'contact', component: ContactComponent },
      { path: 'committee', component: CommitteeComponent },
      { path: 'faq', component: FaqComponent },
      { path: 'prices', component: PricesComponent },
      { path: '**', component: NotFoundComponent }
    ]
  }
];
