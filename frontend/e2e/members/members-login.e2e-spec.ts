import {MembersLoginPage} from './members-login-page.po';

describe('Members login', function () {

  let page: MembersLoginPage;
  let baseUrl: string;

  beforeEach(() => {
    page = new MembersLoginPage();
    baseUrl = page.baseUrl();
  });

  afterEach(() => {
    page.syncOn();
  });

  it('will allow a new user to login with Facebook', (done) => {
    page.navigateTo();

    expect(page.loginIntroText().getText()).toEqual('Login to view your course booking and membership information.');

    page.syncOff();
    page.loginButton().click();

    page.waitForFacebookLoginPage();
    expect(page.getCurrentUrl()).toMatch(/https:\/\/www.facebook.com\/login.php\?.*/);

    page.fbUsernameBox().sendKeys('miwnvzsxjg_1500406593@tfbnw.net');
    page.fbPasswordBox().sendKeys(process.env['FB_TEST_USER_PASS']);
    page.fbLoginButton().click();

    page.waitForAppPage();

    page.syncOn();

    page.waitForDashboard().then(() => {
      expect(page.getCurrentUrl()).toMatch(/.*members\/dashboard$/);
      expect(page.profile().getText()).toEqual('Hello, Richard!');

      done();
    }).catch(done);
  });

});
