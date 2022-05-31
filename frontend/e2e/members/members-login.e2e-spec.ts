import { MembersLoginPage } from "./members-login-page.po";

describe("Members login", function () {
  let page: MembersLoginPage;
  let baseUrl: string;

  beforeEach(() => {
    page = new MembersLoginPage();
    baseUrl = page.baseUrl();
  });

  afterEach(() => {
    return page.syncOn();
  });

  it("will allow a new user to login with Facebook", async () => {
    await page.navigateTo();

    expect(await page.loginIntroText().getText()).toEqual(
      "Login to view your course booking and membership information."
    );

    await page.syncOff();
    await page.loginButton().click();

    await page.waitForFacebookLoginPage();
    expect(await page.getCurrentUrl()).toMatch(
      /https:\/\/www.facebook.com\/login.php\?.*/
    );

    expect(await page.fbUsernameBox().isPresent()).toBeTruthy();
    await page.fbUsernameBox().sendKeys("miwnvzsxjg_1500406593@tfbnw.net");
    await page.fbPasswordBox().sendKeys(process.env["FB_TEST_USER_PASS"]);
    await page.fbLoginButton().click();

    await page.waitForAppPage();

    await page.syncOn();

    await page.waitForDashboard();

    expect(await page.getCurrentUrl()).toMatch(/.*members\/dashboard$/);
    expect(await page.profile().getText()).toEqual("Hello, Richard!");
  });
});
