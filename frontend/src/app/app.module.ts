/// <reference path="../typings.d.ts" />

import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import {
  FormsModule,
  FormBuilder,
  ReactiveFormsModule
} from '@angular/forms';
import { HttpModule    } from '@angular/http';
import { AppComponent } from './app.component';
import { routing } from './app.routing';
import { LocationStrategy, PathLocationStrategy } from '@angular/common';

import {
  BaseComponent,
  HomeComponent,
  AboutComponent,
  ContactComponent,
  CommitteeComponent,
  FaqComponent,
  PricesComponent,
  PageNavComponent
} from './pages';

@NgModule({
  imports:      [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    routing
  ],
  declarations: [
    AppComponent,
    PageNavComponent,

    HomeComponent,
    BaseComponent,
    AboutComponent,
    ContactComponent,
    CommitteeComponent,
    FaqComponent,
    PricesComponent
  ],
  bootstrap: [
    AppComponent
  ],
  providers: [
    FormBuilder,
    { provide: LocationStrategy, useClass: PathLocationStrategy }
  ]
})
export class AppModule { }
