/// <reference path="../typings.d.ts" />

import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {
  FormsModule,
  FormBuilder,
  ReactiveFormsModule
} from '@angular/forms';
import {HttpModule} from '@angular/http';
import {LocationStrategy, PathLocationStrategy} from '@angular/common';
import {Angulartics2Module, Angulartics2GoogleAnalytics} from 'angulartics2';

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
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
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
    FormBuilder,
    {provide: LocationStrategy, useClass: PathLocationStrategy}
  ]
})
export class AppModule {
}
