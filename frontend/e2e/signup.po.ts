import { browser, element, by } from "protractor";

import { createJwt } from "./util";

export class SignupForm {
  navigateTo() {
    // Navigate to homepage to allow setting of local storage data, then go to sign-up page
    return Promise.all([
      browser.get("/"),
      browser.executeScript(
        'window.localStorage.setItem("IS_COMMITTEE", "true")'
      ),
      browser.executeScript(
        'window.localStorage.setItem("JWT", "' + createJwt() + '")'
      ),
      browser.get("/members/committee/sign-up")
    ]);
  }

  getCurrentUrl() {
    return browser.getCurrentUrl();
  }

  nameField() {
    return element(by.css("#name"));
  }

  nameFieldError() {
    return element(by.css("#name-form-field-error"));
  }

  phoneNumberField() {
    return element(by.css("#phone-number"));
  }

  phoneNumberFieldError() {
    return element(by.css("#phone-number-form-field-error"));
  }

  serverPhoneNumberErrors() {
    return element(by.css("#server-phone-number-errors"));
  }

  generalErrors() {
    return element(by.css("#general-errors"));
  }

  submitButton() {
    return element(by.css("button[type=submit]"));
  }

  baseUrl() {
    return browser.baseUrl;
  }
}
