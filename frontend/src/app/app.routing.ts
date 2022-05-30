import { ModuleWithProviders } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";

import { PageRoutes } from "./pages/page.routes";
import { NotFoundComponent } from "./pages/not-found/not-found.component";

export const routes: Routes = [
  ...PageRoutes,
  {
    path: "admin",
    loadChildren: () =>
      import("./admin/admin.module").then(mod => mod.AdminModule)
  },
  {
    path: "members",
    loadChildren: () =>
      import("./members/members.module").then(mod => mod.MembersModule)
  },
  { path: "**", component: NotFoundComponent }
];

export const routing: ModuleWithProviders<unknown> = RouterModule.forRoot(
  routes
);
