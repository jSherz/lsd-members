import {browser, element, by} from 'protractor';

import {createJwt} from './util';

export class SignupAltForm {

  getCurrentUrl() {
    return browser.getCurrentUrl();
  }

  navigateTo() {
    return Promise.all([
      browser.get('/members/committee/sign-up/alt'),
      browser.executeScript('window.localStorage.setItem("IS_COMMITTEE", "true")'),
      browser.executeScript('window.localStorage.setItem("JWT", "' + createJwt() + '")')
    ]);
  }

  nameField() {
    return element(by.css('#name'));
  }

  nameFieldError() {
    return element(by.css('#name-form-field-error'));
  }

  emailField() {
    return element(by.css('#email'));
  }

  emailFieldError() {
    return element(by.css('#email-form-field-error'));
  }

  submitButton() {
    return element(by.css('button'));
  }

  serverEmailErrors() {
    return element(by.css('#server-email-errors'));
  }

  baseUrl() {
    return browser.baseUrl;
  }

}
