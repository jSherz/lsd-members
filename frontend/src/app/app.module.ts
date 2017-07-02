import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
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
  PageNavComponent,
  NotFoundComponent
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
    PricesComponent,
    NotFoundComponent
  ],
  bootstrap: [
    AppComponent
  ],
  providers: [
    {provide: LocationStrategy, useClass: PathLocationStrategy}
  ]
})
export class AppModule {
}
