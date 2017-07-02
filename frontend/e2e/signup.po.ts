import {browser, element, by} from 'protractor';

export class SignupForm {

  navigateTo() {
    browser.executeScript('window.localStorage.setItem("API_KEY", "7e9d3d99-0bfc-4401-882b-1d32c46de3b9")');
    return browser.get('/admin/sign-up');
  }

  getCurrentUrl() {
    return browser.getCurrentUrl();
  }

  nameField() {
    return element(by.css('#name'));
  }

  nameFieldError() {
    return element(by.css('#name-form-field-error'));
  }

  phoneNumberField() {
    return element(by.css('#phone-number'));
  }

  phoneNumberFieldError() {
    return element(by.css('#phone-number-form-field-error'));
  }

  serverPhoneNumberErrors() {
    return element(by.css('#server-phone-number-errors'));
  }

  generalErrors() {
    return element(by.css('#general-errors'));
  }

  submitButton() {
    return element(by.css('button[type=submit]'));
  }

  baseUrl() {
    return browser.baseUrl;
  }

}
