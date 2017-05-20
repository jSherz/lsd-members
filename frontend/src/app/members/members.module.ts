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
import {DashboardComponent, ProfileComponent, DashboardService, DashboardServiceImpl} from './dashboard';
import {NotApprovedComponent} from './not-approved/not-approved.component';
import {
  SocialLoginService,
  SocialLoginServiceImpl,
  PerformLoginComponent
} from './social-login';
import {JwtLoginService, JwtLoginServiceImpl, JwtService, JwtServiceImpl} from './login';

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
    NotApprovedComponent,
    ProfileComponent,
    PerformLoginComponent
  ],
  bootstrap: [],
  providers: [
    FormBuilder,
    {provide: SocialLoginService, useClass: SocialLoginServiceImpl},
    {provide: JwtLoginService, useClass: JwtLoginServiceImpl},
    {provide: JwtService, useClass: JwtServiceImpl},
    {provide: DashboardService, useClass: DashboardServiceImpl}
  ]
})
export class MembersModule {
}
