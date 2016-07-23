export class SignupAltForm {
  navigateTo() {
    return browser.get('/sign-up/alt');
  }

  nameField() {
    return element(by.css('#name'));
  }

  emailField() {
    return element(by.css('#email'));
  }

  submitButton() {
    return element(by.css('button[type=submit]'));
  }
}
