import { bootstrap } from '@angular/platform-browser-dynamic';
import { enableProdMode } from '@angular/core';
import { disableDeprecatedForms, provideForms } from '@angular/forms';
import { AppComponent, environment, APP_ROUTER_PROVIDERS } from './app/';
import {CourseService, CourseServiceImpl} from "./app/admin/courses/course.service";

if (environment.production) {
  enableProdMode();
}

bootstrap(AppComponent, [
  disableDeprecatedForms(),
  provideForms(),
  APP_ROUTER_PROVIDERS
]);
