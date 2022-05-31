import { LoginPage } from "./login.po";

describe("Login", function () {
  let page: LoginPage;
  let baseUrl: string;

  beforeEach(() => {
    page = new LoginPage();
    baseUrl = page.baseUrl();
  });

  it("has a disabled login button when no fields are filled in", async () => {
    await page.navigateTo();

    expect(await page.submitButton().isEnabled()).toBeFalsy();
  });

  it("not show validation failed messages when the user has not touched the fields", async () => {
    await page.navigateTo();

    expect(await page.emailFieldError().isDisplayed()).toBeFalsy();
    expect(await page.passwordFieldError().isDisplayed()).toBeFalsy();
  });

  it("should show a validation error if no e-mail is entered", async () => {
    await page.navigateTo();

    await page.emailField().click();
    await page.passwordField().click();

    expect(await page.submitButton().isEnabled()).toBeFalsy();
    expect(await page.emailFieldError().isDisplayed()).toBeTruthy();
    expect(await page.emailFieldError().getText()).toEqual(
      "Please enter a valid e-mail"
    );
  });

  it("should show a validation error if no password is entered", async () => {
    await page.navigateTo();

    await page.passwordField().click();
    await page.emailField().click();

    expect(await page.submitButton().isEnabled()).toBeFalsy();
    expect(await page.passwordFieldError().isDisplayed()).toBeTruthy();
    expect(await page.passwordFieldError().getText()).toEqual(
      "Please enter your password"
    );
  });

  it("should enable the submit button if a valid e-mail and password are entered", async () => {
    await page.navigateTo();

    await page.emailField().click();
    await page.emailField().sendKeys("test@example.com");
    await page.passwordField().click();
    await page.passwordField().sendKeys("Hunter2");

    expect(await page.submitButton().isEnabled()).toBeTruthy();
    expect(await page.emailFieldError().isDisplayed()).toBeFalsy();
    expect(await page.passwordFieldError().isDisplayed()).toBeFalsy();
  });

  it("clears validation errors (password) after resolving the issue", async () => {
    await page.navigateTo();

    await page.passwordField().click();
    await page.emailField().click();
    expect(await page.passwordFieldError().isDisplayed()).toBeTruthy();

    await page.passwordField().sendKeys("Passw0rd!");
    expect(await page.passwordFieldError().isDisplayed()).toBeFalsy();
  });

  it("navigates the user to the calendar if login succeeds", async () => {
    await page.navigateTo();

    await page.emailField().sendKeys("jlambert@gutierrez-lester.com");
    await page
      .passwordField()
      .sendKeys("jlambert@gutierrez-lester.com_Hunter2");

    await page.submitButton().click();

    expect(await page.currentUrl()).toEqual(baseUrl + "admin/courses/calendar");
  });
});
