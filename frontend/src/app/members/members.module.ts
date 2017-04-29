import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpModule} from '@angular/http';

import {
  FormsModule,
  FormBuilder,
  ReactiveFormsModule
} from '@angular/forms';

import {membersRouting} from './members.routing';
import {MembersComponent} from './members.component';
import {LoginComponent} from './login';
import {DashboardComponent} from './dashboard';
import {NotApprovedComponent} from './not-approved/not-approved.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    membersRouting
  ],
  declarations: [
    MembersComponent,
    LoginComponent,
    DashboardComponent,
    NotApprovedComponent
  ],
  bootstrap: [],
  providers: [
    FormBuilder
  ]
})
export class MembersModule {
}
