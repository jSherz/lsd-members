import { SignupAltForm } from './signup-alt.po';

describe('Alternative sign-up form', function() {
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
  /*a
  it('clears validation errors after resolving the issue',
    inject([SignupAltComponent], (app: SignupAltComponent) => {

    app.signup();

    expect(app.validationErrors['name']).toEqual('Please enter a name');
    expect(app.validationErrors['email']).toEqual('Please enter an e-mail');

    app.user.name = 'Madison Booth'
    app.user.email = 'MadisonBooth@teleworm.us'

    app.signup();

    expect(app.validationErrors['name']).toBeUndefined();
    expect(app.validationErrors['email']).toBeUndefined();
  }));
*/
});


