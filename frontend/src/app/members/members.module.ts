import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpModule} from '@angular/http';
import {FacebookModule} from 'ngx-facebook';

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
import {SocialLoginService, SocialLoginServiceImpl} from './social-login/social-login.service';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    FacebookModule.forRoot(),
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
    FormBuilder,
    {provide: SocialLoginService, useClass: SocialLoginServiceImpl}
  ]
})
export class MembersModule {
}
