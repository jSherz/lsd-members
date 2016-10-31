import {browser, element, by} from 'protractor';

export class SignupAltForm {
  navigateTo() {
    browser.executeScript('window.localStorage.setItem("API_KEY", "5a584fc1-8592-420d-ba5c-1d3254de7cbb")');
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
}
