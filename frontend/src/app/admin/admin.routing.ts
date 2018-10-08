import { ModuleWithProviders } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";

import {
  CanActivateAdmin,
  AdminBaseComponent,
  LoginComponent,
  CourseCalendarComponent,
  CourseViewComponent,
  CourseAddComponent,
  MassTextComponent,
  MemberViewComponent,
  MemberLookupComponent,
  MemberEditComponent,
  MemberApprovalComponent
} from "./";

export const adminRoutes: Routes = [
  {
    path: "",
    component: AdminBaseComponent,
    children: [
      { path: "", redirectTo: "login", pathMatch: "full" },
      { path: "login", component: LoginComponent },
      {
        path: "courses/calendar/:year/:month",
        component: CourseCalendarComponent,
        canActivate: [CanActivateAdmin]
      },
      {
        path: "courses/calendar",
        component: CourseCalendarComponent,
        canActivate: [CanActivateAdmin]
      },
      {
        path: "courses/add",
        component: CourseAddComponent,
        canActivate: [CanActivateAdmin]
      },
      {
        path: "courses/:uuid",
        component: CourseViewComponent,
        canActivate: [CanActivateAdmin]
      },
      {
        path: "mass-text",
        component: MassTextComponent,
        canActivate: [CanActivateAdmin]
      },
      {
        path: "members/lookup",
        component: MemberLookupComponent,
        canActivate: [CanActivateAdmin]
      },
      {
        path: "members/approval",
        component: MemberApprovalComponent,
        canActivate: [CanActivateAdmin]
      },
      {
        path: "members/:uuid/edit",
        component: MemberEditComponent,
        canActivate: [CanActivateAdmin]
      },
      {
        path: "members/:uuid",
        component: MemberViewComponent,
        canActivate: [CanActivateAdmin]
      }
    ]
  }
];

export const adminRouting: ModuleWithProviders = RouterModule.forChild(
  adminRoutes
);
