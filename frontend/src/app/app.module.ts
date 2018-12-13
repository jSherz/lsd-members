import { BrowserModule } from "@angular/platform-browser";
import { InjectionToken, NgModule } from "@angular/core";
import { LocationStrategy, PathLocationStrategy } from "@angular/common";
import { Angulartics2Module } from "angulartics2";
import { Angulartics2GoogleAnalytics } from "angulartics2/ga";

import { AppComponent } from "./app.component";
import { routing } from "./app.routing";

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
} from "./pages";

export const APP_VERSION: InjectionToken<string> = new InjectionToken<string>(
  "APP_VERSION"
);

@NgModule({
  imports: [BrowserModule, routing, Angulartics2Module.forRoot()],
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
  bootstrap: [AppComponent],
  providers: [
    { provide: LocationStrategy, useClass: PathLocationStrategy },
    { provide: APP_VERSION, useValue: "placeholder-version-123" }
  ]
})
export class AppModule {}
