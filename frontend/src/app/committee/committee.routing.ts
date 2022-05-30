import { ModuleWithProviders } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";

import { DashboardComponent } from "./dashboard";
import { CanActivateCommittee } from "./utils";
import {
  SignupBaseComponent,
  SignupComponent,
  SignupAltComponent,
  ThankYouComponent
} from "./signup";

export const committeeRoutes: Routes = [
  {
    path: "",
    children: [
      { path: "", redirectTo: "dashboard" },
      {
        path: "dashboard",
        component: DashboardComponent,
        canActivate: [CanActivateCommittee]
      },
      {
        path: "sign-up",
        component: SignupBaseComponent,
        children: [
          { path: "", component: SignupComponent },
          { path: "alt", component: SignupAltComponent },
          { path: "thank-you", component: ThankYouComponent }
        ],
        canActivate: [CanActivateCommittee]
      }
    ]
  }
];

export const committeeRouting: ModuleWithProviders<unknown> = RouterModule.forChild(
  committeeRoutes
);
