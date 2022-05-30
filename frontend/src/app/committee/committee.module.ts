import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { HttpClientModule } from "@angular/common/http";

import { FormsModule, FormBuilder, ReactiveFormsModule } from "@angular/forms";

import { committeeRouting } from "./committee.routing";

import {
  DashboardComponent,
  DashboardStatsComponent,
  DashboardStatsService,
  DashboardStatsServiceImpl
} from "./dashboard";

import { HeaderComponent } from "./header";
import { CanActivateCommittee } from "./utils";

import {
  SignupBaseComponent,
  SignupComponent,
  SignupAltComponent,
  ThankYouComponent,
  SignupService,
  SignupServiceImpl
} from "./signup";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    committeeRouting
  ],
  declarations: [
    DashboardComponent,
    DashboardStatsComponent,
    HeaderComponent,
    SignupBaseComponent,
    SignupComponent,
    SignupAltComponent,
    ThankYouComponent
  ],
  providers: [
    FormBuilder,
    CanActivateCommittee,
    { provide: DashboardStatsService, useClass: DashboardStatsServiceImpl },
    { provide: SignupService, useClass: SignupServiceImpl }
  ]
})
export class CommitteeModule {}
