import { ModuleWithProviders } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";

import { MembersComponent } from "./members.component";
import { LoginComponent } from "./login";
import { DashboardComponent } from "./dashboard";
import { NotApprovedComponent } from "./not-approved";
import { PerformLoginComponent } from "./social-login";
import { CanActivateMembers, CanActivateMembersLogin } from "./utils";
import { ReceivedMessagesComponent } from "./received-messages";

export const membersRoutes: Routes = [
  {
    path: "",
    component: MembersComponent,
    children: [
      {
        path: "",
        component: LoginComponent,
        canActivate: [CanActivateMembersLogin]
      },
      {
        path: "dashboard",
        component: DashboardComponent,
        canActivate: [CanActivateMembers]
      },
      { path: "not-approved", component: NotApprovedComponent },
      { path: "perform-login", component: PerformLoginComponent },
      {
        path: "text-messages/received",
        component: ReceivedMessagesComponent,
        canActivate: [CanActivateMembers]
      },
      {
        path: "committee",
        loadChildren: "app/committee/committee.module#CommitteeModule"
      }
    ]
  }
];

export const membersRouting: ModuleWithProviders = RouterModule.forChild(
  membersRoutes
);
