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

/*
  it('should show an error when attempting to sign-up with no name or e-mail', () => {
    page.navigateTo();

    page.submitButton().click();

    expect(app.user.name.valid).toBeFalse();
    expect(app.user.email.valid).toBeFalse();
  });

  it('should not show an error when attempting to sign-up with a valid name and e-mail',
    inject([SignupAltComponent], (app: SignupAltComponent) => {

      app.user.name = 'Kiera Barker'
      app.user.email = 'KieraBarker@rhyta.com'

      app.signup();

      expect(app.validationErrors['name']).toBeUndefined();
      expect(app.validationErrors['email']).toBeUndefined();
  }));

  it('should not accept invalid e-mails',
    inject([SignupAltComponent], (app: SignupAltComponent) => {

    app.user.name = 'Robert Burgess'
    app.user.email = 'RobertBurgess@localhost'

    app.signup();

    expect(app.validationErrors['name']).toBeUndefined();
    expect(app.validationErrors['email']).toEqual('Please enter a valid e-mail');
  }));

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


