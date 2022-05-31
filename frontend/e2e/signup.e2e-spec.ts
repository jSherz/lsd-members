import { SignupForm } from "./signup.po";

describe("Sign-up form", function () {
  let page: SignupForm;
  let baseUrl: string;

  beforeEach(() => {
    page = new SignupForm();
    baseUrl = page.baseUrl();
  });

  it("has a disabled sign-up button when no fields are filled in", async () => {
    await page.navigateTo();

    expect(await page.submitButton().isEnabled()).toBeFalsy();
  });

  it("not show validation failed messages when the user has not touched the fields", async () => {
    await page.navigateTo();

    expect(await page.nameFieldError().isDisplayed()).toBeFalsy();
    expect(await page.phoneNumberFieldError().isDisplayed()).toBeFalsy();
  });

  it("should show a validation error if no name is entered", async () => {
    await page.navigateTo();

    await page.nameField().click();
    await page.phoneNumberField().click();

    expect(await page.submitButton().isEnabled()).toBeFalsy();
    expect(await page.nameFieldError().isDisplayed()).toBeTruthy();
    expect(await page.nameFieldError().getText()).toEqual(
      "Please enter a name"
    );
  });

  it("should show a validation error if no phone number is entered", async () => {
    await page.navigateTo();

    await page.phoneNumberField().click();
    await page.nameField().click();

    expect(await page.submitButton().isEnabled()).toBeFalsy();
    expect(await page.phoneNumberFieldError().isDisplayed()).toBeTruthy();
    expect(await page.phoneNumberFieldError().getText()).toEqual(
      "Please enter a valid phone number"
    );
  });

  it("should show a validation error if an invalid phone number is entered", async () => {
    await page.navigateTo();

    await page.phoneNumberField().click();
    await page.phoneNumberField().sendKeys("0712312a123");
    await page.nameField().click();
    await page.nameField().sendKeys("Bob");

    expect(await page.submitButton().isEnabled()).toBeFalsy();
    expect(await page.phoneNumberFieldError().isDisplayed()).toBeTruthy();
    expect(await page.phoneNumberFieldError().getText()).toEqual(
      "Please enter a valid phone number"
    );
  });

  it("should enable the submit button if a valid name and phone number are entered", async () => {
    await page.navigateTo();

    await page.nameField().click();
    await page.nameField().sendKeys("Jennifer Carey");
    await page.phoneNumberField().click();
    await page.phoneNumberField().sendKeys("07010101010");

    expect(await page.submitButton().isEnabled()).toBeTruthy();
    expect(await page.nameFieldError().isDisplayed()).toBeFalsy();
    expect(await page.phoneNumberFieldError().isDisplayed()).toBeFalsy();
  });

  it("clears validation errors (name) after resolving the issue", async () => {
    await page.navigateTo();

    await page.nameField().click();
    await page.phoneNumberField().click();
    expect(await page.nameFieldError().isDisplayed()).toBeTruthy();

    await page.nameField().sendKeys("Sofia O'Connor");
    expect(await page.nameFieldError().isDisplayed()).toBeFalsy();
  });

  it("clears validation errors (phone number) after resolving the issue", async () => {
    await page.navigateTo();

    await page.phoneNumberField().click();
    await page.nameField().click();
    expect(await page.phoneNumberFieldError().isDisplayed()).toBeTruthy();

    await page.phoneNumberField().sendKeys("07111000111");
    expect(await page.phoneNumberFieldError().isDisplayed()).toBeFalsy();
  });

  it("actually signs the user up", async () => {
    const phoneNum =
      "070001" + ("00000" + Math.floor(Math.random() * 10000)).slice(-5);

    await page.navigateTo();

    await page.nameField().sendKeys("Testy McTesting");
    await page.phoneNumberField().sendKeys(phoneNum);
    await page.nameField().click(); // Ensure button is enabled after form fill
    await page.submitButton().click();

    expect(await page.getCurrentUrl()).toEqual(
      baseUrl + "members/committee/sign-up/thank-you"
    );

    await page.navigateTo();

    await page.nameField().sendKeys("Testy McTesting");
    await page.phoneNumberField().sendKeys(phoneNum);
    await page.nameField().click(); // Ensure button is enabled after form fill
    await page.submitButton().click();

    expect(await page.serverPhoneNumberErrors().getText()).toEqual(
      "This phone number is in use. Have you already signed up?"
    );
  });
});
