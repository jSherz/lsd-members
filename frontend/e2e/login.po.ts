import {browser, element, by} from 'protractor';

export class LoginPage {

  navigateTo() {
    return browser.get('/admin/login');
  }

  currentUrl() {
    return browser.getCurrentUrl();
  }

  emailField() {
    return element(by.css('#email'));
  }

  emailFieldError() {
    return element(by.css('#email-form-field-error'));
  }

  passwordField() {
    return element(by.css('#phone-number'));
  }

  passwordFieldError() {
    return element(by.css('#phone-number-form-field-error'));
  }

  submitButton() {
    return element(by.css('button[type=submit]'));
  }

}
