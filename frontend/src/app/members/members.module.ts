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
import {
  JwtLoginService,
  JwtLoginServiceImpl,
  JwtService,
  JwtServiceImpl
} from './login';
import {KeyInfoComponent} from './key-info/key-info.component';
import {
  PackingListComponent,
  PackingListService,
  PackingListServiceImpl
} from './packing-list';
import {TheWeatherComponent} from './dashboard';
import {CanActivateMembers, CanActivateMembersLogin} from './utils';
import {HeaderComponent} from './header/header.component';
import {
  ReceivedMessagesComponent,
  TextMessagesService,
  TextMessagesServiceImpl
} from './received-messages';

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
    PerformLoginComponent,
    KeyInfoComponent,
    PackingListComponent,
    TheWeatherComponent,
    HeaderComponent,
    ReceivedMessagesComponent
  ],
  bootstrap: [],
  providers: [
    FormBuilder,
    {provide: SocialLoginService, useClass: SocialLoginServiceImpl},
    {provide: JwtLoginService, useClass: JwtLoginServiceImpl},
    {provide: JwtService, useClass: JwtServiceImpl},
    {provide: DashboardService, useClass: DashboardServiceImpl},
    {provide: PackingListService, useClass: PackingListServiceImpl},
    {provide: TextMessagesService, useClass: TextMessagesServiceImpl},
    CanActivateMembers,
    CanActivateMembersLogin
  ]
})
export class MembersModule {
}
