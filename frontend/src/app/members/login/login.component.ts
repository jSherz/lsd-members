import { Component } from "@angular/core";

import { environment } from "../../../environments/environment";

@Component({
  selector: "lsd-members-login",
  templateUrl: "login.component.html",
  styleUrls: ["login.component.sass"]
})
export class LoginComponent {
  private returnUrl = encodeURIComponent(
    environment.baseUrl + "/members/perform-login"
  );

  loginUrl: string =
    `https://www.facebook.com/dialog/oauth?client_id=${
      environment.fbClientId
    }` +
    `&redirect_uri=${this.returnUrl}` +
    "&scope=public_profile%2Cemail";
}
