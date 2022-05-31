import { SignupAltForm } from "./signup-alt.po";

describe("Alternative sign-up form", function () {
  let page: SignupAltForm;
  let baseUrl: string;

  beforeEach(() => {
    page = new SignupAltForm();
    baseUrl = page.baseUrl();
  });

  it("has a disabled sign-up button when no fields are filled in", async () => {
    await page.navigateTo();

    expect(await page.submitButton().isEnabled()).toBeFalsy();
  });

  it("not show validation failed messages when the user has not touched the fields", async () => {
    await page.navigateTo();

    expect(await page.nameFieldError().isDisplayed()).toBeFalsy();
    expect(await page.emailFieldError().isDisplayed()).toBeFalsy();
  });

  it("should show a validation error if no name is entered", async () => {
    await page.navigateTo();

    await page.nameField().click();
    await page.emailField().click();

    expect(await page.submitButton().isEnabled()).toBeFalsy();
    expect(await page.nameFieldError().isDisplayed()).toBeTruthy();
    expect(await page.nameFieldError().getText()).toEqual(
      "Please enter a name"
    );
  });

  it("should show a validation error if no e-mail is entered", async () => {
    await page.navigateTo();

    await page.emailField().click();
    await page.nameField().click();

    expect(await page.submitButton().isEnabled()).toBeFalsy();
    expect(await page.emailFieldError().isDisplayed()).toBeTruthy();
    expect(await page.emailFieldError().getText()).toEqual(
      "Please enter a valid e-mail address"
    );
  });

  it("should show a validation error if an invalid e-mail is entered", async () => {
    await page.navigateTo();

    await page.emailField().click();
    await page.emailField().sendKeys("bob.ntlworld.com");
    await page.nameField().click();
    await page.nameField().sendKeys("Bob");

    expect(await page.submitButton().isEnabled()).toBeFalsy();
    expect(await page.emailFieldError().isDisplayed()).toBeTruthy();
    expect(await page.emailFieldError().getText()).toEqual(
      "Please enter a valid e-mail address"
    );
  });

  it("should enable the submit button if a valid name and e-mail are entered", async () => {
    await page.navigateTo();

    await page.nameField().click();
    await page.nameField().sendKeys("Jennifer Carey");
    await page.emailField().click();
    await page.emailField().sendKeys("JenniferCarey@dayrep.com");

    expect(await page.submitButton().isEnabled()).toBeTruthy();
    expect(await page.nameFieldError().isDisplayed()).toBeFalsy();
    expect(await page.emailFieldError().isDisplayed()).toBeFalsy();
  });

  it("clears validation errors (name) after resolving the issue", async () => {
    await page.navigateTo();

    await page.nameField().click();
    await page.emailField().click();
    expect(await page.nameFieldError().isDisplayed()).toBeTruthy();

    await page.nameField().sendKeys("Sofia O'Connor");
    expect(await page.nameFieldError().isDisplayed()).toBeFalsy();
  });

  it("clears validation errors (e-mail) after resolving the issue", async () => {
    await page.navigateTo();

    await page.emailField().click();
    await page.nameField().click();
    expect(await page.emailFieldError().isDisplayed()).toBeTruthy();

    await page.emailField().sendKeys("SofiaOConnor@teleworm.us");
    expect(await page.emailFieldError().isDisplayed()).toBeFalsy();
  });

  it("actually signs the user up", async () => {
    const now = new Date();
    const emailSuffix =
      now.getUTCFullYear() + "-" + now.getUTCMonth() + "-" + now.getUTCDate();
    const emailSuffix2 = (Math.random() * 10000)
      .toString()
      .replace(".", "")
      .substring(0, 6);
    const email = `e2e-auto-${emailSuffix}${emailSuffix2}@mctesting.org.uk`;

    await page.navigateTo();

    await page.nameField().sendKeys("Testy McTesting");
    await page.emailField().sendKeys(email);
    await page.nameField().click(); // Ensure button is enabled after form fill
    await page.submitButton().click();

    expect(await page.getCurrentUrl()).toEqual(
      baseUrl + "members/committee/sign-up/thank-you"
    );

    await page.navigateTo();

    await page.nameField().sendKeys("Testy McTesting");
    await page.emailField().sendKeys(email);
    await page.nameField().click(); // Ensure button is enabled after form fill
    await page.submitButton().click();

    expect(await page.serverEmailErrors().getText()).toEqual(
      "This e-mail is in use. Have you already signed up?"
    );
  });
});
