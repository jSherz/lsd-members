/// <reference path="../typings.d.ts" />

import {NgModule, ErrorHandler} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {LocationStrategy, PathLocationStrategy} from '@angular/common';
import {Angulartics2Module, Angulartics2GoogleAnalytics} from 'angulartics2';
import {RavenErrorHandler} from './utils/error-handler';

import {AppComponent} from './app.component';
import {routing} from './app.routing';

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
  imports: [
    BrowserModule,
    routing,
    Angulartics2Module.forRoot([Angulartics2GoogleAnalytics])
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
    {provide: LocationStrategy, useClass: PathLocationStrategy} //,
    // {provide: ErrorHandler, useClass: RavenErrorHandler}
  ]
})
export class AppModule {
}
