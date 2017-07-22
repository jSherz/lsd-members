import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpModule} from '@angular/http';

import {
  FormsModule,
  FormBuilder,
  ReactiveFormsModule
} from '@angular/forms';

import {committeeRouting} from './committee.routing';

import {
  DashboardComponent,
  DashboardStatsComponent,
  DashboardStatsService,
  DashboardStatsServiceImpl
} from './dashboard';

import {HeaderComponent} from './header';
import {CanActivateCommittee} from './utils';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    committeeRouting
  ],
  declarations: [
    DashboardComponent,
    DashboardStatsComponent,
    HeaderComponent
  ],
  providers: [
    FormBuilder,
    CanActivateCommittee,
    {provide: DashboardStatsService, useClass: DashboardStatsServiceImpl}
  ]
})
export class CommitteeModule {
}
