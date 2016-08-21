/// <reference path="../typings.d.ts" />

import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {FormsModule, FORM_PROVIDERS, FormBuilder} from '@angular/forms';
import { HttpModule    } from '@angular/http';

import { AppComponent } from './app.component';
import { routing      } from './app.routes';
import {CourseAddComponent} from "./admin/courses/course-add/course-add.component";
import {TileComponent} from "./admin/courses/course-calendar/tile/tile.component";
import {CourseCalendarComponent} from "./admin/courses/course-calendar/course-calendar.component";
import {CourseViewComponent} from "./admin/courses/course-view/course-view.component";
import {MemberSearchComponent} from "./admin/member-search/member-search.component";
import {AdminBaseComponent} from "./admin/admin-base.component";
import {SignupBaseComponent} from "./signup/signup-base.component";
import {SignupComponent} from "./signup/main/signup.component";
import {SignupAltComponent} from "./signup/alt/signup-alt.component";
import {SignupService, SignupServiceImpl} from "./signup/service/signup.service";
import {ThankYouComponent} from "./signup/thank-you/thank-you.component";
import {LocationStrategy, PathLocationStrategy} from "@angular/common";
import {MemberSearchService, MemberSearchServiceImpl} from "./admin/member-search/member-search.service";

@NgModule({
  imports:      [
    BrowserModule,
    FormsModule,
    HttpModule,
    routing
  ],
  declarations: [
    AppComponent,

    AdminBaseComponent,
    CourseAddComponent,
    TileComponent,
    CourseCalendarComponent,
    CourseViewComponent,
    MemberSearchComponent,

    SignupBaseComponent,
    SignupComponent,
    SignupAltComponent,
    ThankYouComponent
  ],
  bootstrap: [
    AppComponent
  ],
  providers: [
    FormBuilder,
    { provide: SignupService, useClass: SignupServiceImpl },
    { provide: LocationStrategy, useClass: PathLocationStrategy },
    { provide: MemberSearchService, useClass: MemberSearchServiceImpl }
  ]
})
export class AppModule { }