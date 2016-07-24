import { SignupForm } from './signup.po';

describe('Sign-up form', function() {
  let page: SignupForm;

  beforeEach(() => {
    page = new SignupForm();
  });

  it('has a disabled sign-up button when no fields are filled in', () => {
    page.navigateTo();

    expect(page.submitButton().isEnabled()).toBeFalsy();
  });

  it('not show validation failed messages when the user has not touched the fields', () => {
    page.navigateTo();

    expect(page.nameFieldError().isDisplayed()).toBeFalsy();
    expect(page.phoneNumberFieldError().isDisplayed()).toBeFalsy();
  });

  it('should show a validation error if no name is entered', () => {
    page.navigateTo();

    page.nameField().click();
    page.phoneNumberField().click();

    expect(page.submitButton().isEnabled()).toBeFalsy();
    expect(page.nameFieldError().isDisplayed()).toBeTruthy();
    expect(page.nameFieldError().getText()).toEqual('Please enter a name');
  });

  it('should show a validation error if no phone number is entered', () => {
    page.navigateTo();

    page.phoneNumberField().click();
    page.nameField().click();

    expect(page.submitButton().isEnabled()).toBeFalsy();
    expect(page.phoneNumberFieldError().isDisplayed()).toBeTruthy();
    expect(page.phoneNumberFieldError().getText()).toEqual('Please enter a valid phone number');
  });

  it('should show a validation error if an invalid phone number is entered', () => {
    page.navigateTo();

    page.phoneNumberField().click();
    page.phoneNumberField().sendKeys('0712312a123');
    page.nameField().click();
    page.nameField().sendKeys('Bob');

    expect(page.submitButton().isEnabled()).toBeFalsy();
    expect(page.phoneNumberFieldError().isDisplayed()).toBeTruthy();
    expect(page.phoneNumberFieldError().getText()).toEqual('Please enter a valid phone number');
  });

  it('should enable the submit button if a valid name and phone number are entered', () => {
    page.navigateTo();

    page.nameField().click();
    page.nameField().sendKeys('Jennifer Carey');
    page.phoneNumberField().click();
    page.phoneNumberField().sendKeys('07010101010');

    expect(page.submitButton().isEnabled()).toBeTruthy();
    expect(page.nameFieldError().isDisplayed()).toBeFalsy();
    expect(page.phoneNumberFieldError().isDisplayed()).toBeFalsy();
  });

  it('clears validation errors (name) after resolving the issue', () => {
    page.navigateTo();

    page.nameField().click();
    page.phoneNumberField().click();
    expect(page.nameFieldError().isDisplayed()).toBeTruthy();

    page.nameField().sendKeys('Sofia O\'Connor');
    expect(page.nameFieldError().isDisplayed()).toBeFalsy();
  });

  it('clears validation errors (phone number) after resolving the issue', () => {
    page.navigateTo();

    page.phoneNumberField().click();
    page.nameField().click();
    expect(page.phoneNumberFieldError().isDisplayed()).toBeTruthy();

    page.phoneNumberField().sendKeys('07111000111');
    expect(page.phoneNumberFieldError().isDisplayed()).toBeFalsy();
  });
});
