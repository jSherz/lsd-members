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

import {membersRouting} from './members.routing';
import {LoginComponent} from './login';
import {MembersComponent} from './members.component';

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
    LoginComponent
  ],
  bootstrap: [],
  providers: [
    FormBuilder
  ]
})
export class MembersModule {
}
