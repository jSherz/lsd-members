import {LoginPage} from './login.po';


describe('Login', function () {

  let page: LoginPage;

  beforeEach(() => {
    page = new LoginPage();
  });

  it('has a disabled login button when no fields are filled in', () => {
    page.navigateTo();

    expect(page.submitButton().isEnabled()).toBeFalsy();
  });

  it('not show validation failed messages when the user has not touched the fields', () => {
    page.navigateTo();

    expect(page.emailFieldError().isDisplayed()).toBeFalsy();
    expect(page.passwordFieldError().isDisplayed()).toBeFalsy();
  });

  it('should show a validation error if no e-mail is entered', () => {
    page.navigateTo();

    page.emailField().click();
    page.passwordField().click();

    expect(page.submitButton().isEnabled()).toBeFalsy();
    expect(page.emailFieldError().isDisplayed()).toBeTruthy();
    expect(page.emailFieldError().getText()).toEqual('Please enter a valid e-mail');
  });

  it('should show a validation error if no password is entered', () => {
    page.navigateTo();

    page.passwordField().click();
    page.emailField().click();

    expect(page.submitButton().isEnabled()).toBeFalsy();
    expect(page.passwordFieldError().isDisplayed()).toBeTruthy();
    expect(page.passwordFieldError().getText()).toEqual('Please enter your password');
  });

  it('should enable the submit button if a valid e-mail and password are entered', () => {
    page.navigateTo();

    page.emailField().click();
    page.emailField().sendKeys('test@example.com');
    page.passwordField().click();
    page.passwordField().sendKeys('Hunter2');

    expect(page.submitButton().isEnabled()).toBeTruthy();
    expect(page.emailFieldError().isDisplayed()).toBeFalsy();
    expect(page.passwordFieldError().isDisplayed()).toBeFalsy();
  });

  it('clears validation errors (password) after resolving the issue', () => {
    page.navigateTo();

    page.passwordField().click();
    page.emailField().click();
    expect(page.passwordFieldError().isDisplayed()).toBeTruthy();

    page.passwordField().sendKeys('Passw0rd!');
    expect(page.passwordFieldError().isDisplayed()).toBeFalsy();
  });

  it('navigates the user to the calendar if login succeeds', () => {
    page.navigateTo();

    page.emailField().sendKeys('jlambert@gutierrez-lester.com');
    page.passwordField().sendKeys('jlambert@gutierrez-lester.com_Hunter2');

    page.submitButton().click();

    expect(page.currentUrl()).toEqual('http://localhost:4200/admin/courses/calendar');
  });

});
