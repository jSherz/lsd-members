import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpModule} from '@angular/http';

import {
  LocationStrategy,
  PathLocationStrategy
} from '@angular/common';

import {
  FormsModule,
  FormBuilder,
  ReactiveFormsModule
} from '@angular/forms';

import {
  NavComponent,
  CanActivateAdmin,
  AdminBaseComponent,
  LoginService,
  LoginServiceImpl,
  LoginComponent,
  CourseAddComponent,
  TileComponent,
  CourseCalendarComponent,
  CourseViewComponent,
  MemberSearchComponent,
  MassTextService,
  MassTextServiceImpl,
  MassTextComponent,
  ApiKeyService,
  ApiKeyServiceImpl,
  SignupService,
  SignupServiceImpl,
  MemberSearchService,
  MemberSearchServiceImpl,
  MemberLookupComponent,
  MemberViewComponent,
  CourseService,
  CourseServiceImpl,
  CourseSpaceService,
  CourseSpaceServiceImpl,
  CommitteeService,
  CommitteeServiceImpl,
  SignupBaseComponent,
  SignupComponent,
  SignupAltComponent,
  ThankYouComponent,
  MemberViewServiceImpl,
  MemberViewService
} from './';

import {adminRouting} from './admin.routing';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    adminRouting
  ],
  declarations: [
    NavComponent,
    AdminBaseComponent,
    LoginComponent,
    CourseAddComponent,
    TileComponent,
    CourseCalendarComponent,
    CourseViewComponent,
    MemberSearchComponent,
    MassTextComponent,
    SignupBaseComponent,
    SignupComponent,
    SignupAltComponent,
    ThankYouComponent,
    MemberViewComponent,
    MemberLookupComponent
  ],
  bootstrap: [],
  providers: [
    FormBuilder,
    {provide: ApiKeyService, useClass: ApiKeyServiceImpl},
    {provide: SignupService, useClass: SignupServiceImpl},
    {provide: LocationStrategy, useClass: PathLocationStrategy},
    {provide: MemberSearchService, useClass: MemberSearchServiceImpl},
    {provide: CourseService, useClass: CourseServiceImpl},
    {provide: CourseSpaceService, useClass: CourseSpaceServiceImpl},
    {provide: CommitteeService, useClass: CommitteeServiceImpl},
    {provide: LoginService, useClass: LoginServiceImpl},
    {provide: MassTextService, useClass: MassTextServiceImpl},
    {provide: MemberViewService, useClass: MemberViewServiceImpl},
    CanActivateAdmin
  ]
})
export class AdminModule {
}
