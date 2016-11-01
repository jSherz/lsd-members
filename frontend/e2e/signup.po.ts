import {browser, element, by} from 'protractor';

export class SignupForm {

  navigateTo() {
    browser.executeScript('window.localStorage.setItem("API_KEY", "5a584fc1-8592-420d-ba5c-1d3254de7cbb")');
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

  generalErrors() {
    return element(by.css('#general-errors'));
  }

  submitButton() {
    return element(by.css('button[type=submit]'));
  }

}
