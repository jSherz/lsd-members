import {browser, element, by} from 'protractor';

export class SignupAltForm {

  getCurrentUrl() {
    return browser.getCurrentUrl();
  }

  navigateTo() {
    browser.executeScript('window.localStorage.setItem("API_KEY", "7e9d3d99-0bfc-4401-882b-1d32c46de3b9")');
    return browser.get('/admin/sign-up/alt');
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
