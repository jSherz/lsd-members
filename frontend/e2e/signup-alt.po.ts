export class SignupAltForm {
  navigateTo() {
    return browser.get('/sign-up/alt');
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
    return element(by.css('button[type=submit]'));
  }
}
