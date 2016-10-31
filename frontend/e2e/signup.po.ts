import {browser, element, by} from 'protractor';

export class SignupForm {
  navigateTo() {
    return browser.get('/sign-up');
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

  submitButton() {
    return element(by.css('button[type=submit]'));
  }
}
