import {SignupAltForm} from './signup-alt.po';

describe('Alternative sign-up form', function () {

  let page: SignupAltForm;

  beforeEach(() => {
    page = new SignupAltForm();
  });

  it('has a disabled sign-up button when no fields are filled in', () => {
    page.navigateTo();

    expect(page.submitButton().isEnabled()).toBeFalsy();
  });

  it('not show validation failed messages when the user has not touched the fields', () => {
    page.navigateTo();

    expect(page.nameFieldError().isDisplayed()).toBeFalsy();
    expect(page.emailFieldError().isDisplayed()).toBeFalsy();
  });

  it('should show a validation error if no name is entered', () => {
    page.navigateTo();

    page.nameField().click();
    page.emailField().click();

    expect(page.submitButton().isEnabled()).toBeFalsy();
    expect(page.nameFieldError().isDisplayed()).toBeTruthy();
    expect(page.nameFieldError().getText()).toEqual('Please enter a name');
  });

  it('should show a validation error if no e-mail is entered', () => {
    page.navigateTo();

    page.emailField().click();
    page.nameField().click();

    expect(page.submitButton().isEnabled()).toBeFalsy();
    expect(page.emailFieldError().isDisplayed()).toBeTruthy();
    expect(page.emailFieldError().getText()).toEqual('Please enter a valid e-mail address');
  });

  it('should show a validation error if an invalid e-mail is entered', () => {
    page.navigateTo();

    page.emailField().click();
    page.emailField().sendKeys('bob.ntlworld.com');
    page.nameField().click();
    page.nameField().sendKeys('Bob');

    expect(page.submitButton().isEnabled()).toBeFalsy();
    expect(page.emailFieldError().isDisplayed()).toBeTruthy();
    expect(page.emailFieldError().getText()).toEqual('Please enter a valid e-mail address');
  });

  it('should enable the submit button if a valid name and e-mail are entered', () => {
    page.navigateTo();

    page.nameField().click();
    page.nameField().sendKeys('Jennifer Carey');
    page.emailField().click();
    page.emailField().sendKeys('JenniferCarey@dayrep.com');

    expect(page.submitButton().isEnabled()).toBeTruthy();
    expect(page.nameFieldError().isDisplayed()).toBeFalsy();
    expect(page.emailFieldError().isDisplayed()).toBeFalsy();
  });

  it('clears validation errors (name) after resolving the issue', () => {
    page.navigateTo();

    page.nameField().click();
    page.emailField().click();
    expect(page.nameFieldError().isDisplayed()).toBeTruthy();

    page.nameField().sendKeys('Sofia O\'Connor');
    expect(page.nameFieldError().isDisplayed()).toBeFalsy();
  });

  it('clears validation errors (e-mail) after resolving the issue', () => {
    page.navigateTo();

    page.emailField().click();
    page.nameField().click();
    expect(page.emailFieldError().isDisplayed()).toBeTruthy();

    page.emailField().sendKeys('SofiaOConnor@teleworm.us');
    expect(page.emailFieldError().isDisplayed()).toBeFalsy();
  });

  it('actually signs the user up', () => {
    page.navigateTo();

    page.nameField().sendKeys('Testy McTesting');
    page.emailField().sendKeys('test@mctesting.org.uk');
    page.nameField().click(); // Ensure button is enabled after form fill
    page.submitButton().click();

    expect(page.getCurrentUrl()).toEqual('http://localhost:49152/admin/sign-up/thank-you');

    page.navigateTo();

    page.nameField().sendKeys('Testy McTesting');
    page.emailField().sendKeys('test@mctesting.org.uk');
    page.nameField().click(); // Ensure button is enabled after form fill
    page.submitButton().click();

    expect(page.serverEmailErrors().getText()).toEqual('This e-mail is in use. Have you already signed up?');
  });

});
