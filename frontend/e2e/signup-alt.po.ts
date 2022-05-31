import { browser, element, by } from "protractor";

import { createJwt } from "./util";

export class SignupAltForm {
  getCurrentUrl() {
    return browser.getCurrentUrl();
  }

  async navigateTo() {
    // Navigate to homepage to allow setting of local storage data, then go to sign-up page
    await browser.get("/");
    await browser.executeScript(
      'window.localStorage.setItem("IS_COMMITTEE", "true")'
    );
    await browser.executeScript(
      'window.localStorage.setItem("JWT", "' + createJwt() + '")'
    );
    await browser.get("/members/committee/sign-up/alt");
  }

  nameField() {
    return element(by.css("#name"));
  }

  nameFieldError() {
    return element(by.css("#name-form-field-error"));
  }

  emailField() {
    return element(by.css("#email"));
  }

  emailFieldError() {
    return element(by.css("#email-form-field-error"));
  }

  submitButton() {
    return element(by.css("button"));
  }

  serverEmailErrors() {
    return element(by.css("#server-email-errors"));
  }

  baseUrl() {
    return browser.baseUrl;
  }
}
