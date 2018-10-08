import { Routes } from "@angular/router";

import { AboutComponent } from "./about";
import { BaseComponent } from "./base";
import { CommitteeComponent } from "./committee";
import { ContactComponent } from "./contact";
import { FaqComponent } from "./faq";
import { HomeComponent } from "./home";
import { PricesComponent } from "./prices";

export const PageRoutes: Routes = [
  {
    path: "",
    component: BaseComponent,
    children: [
      { path: "", component: HomeComponent },
      { path: "about-the-club", component: AboutComponent },
      { path: "contact", component: ContactComponent },
      { path: "committee", component: CommitteeComponent },
      { path: "faq", component: FaqComponent },
      { path: "prices", component: PricesComponent }
    ]
  }
];
