import { LuskydivePage } from "./app.po";

describe("LSD App", function () {
  let page: LuskydivePage;

  beforeEach(() => {
    page = new LuskydivePage();
  });

  it("should display the main form by default", async () => {
    await page.navigateTo();
    expect(await page.getParagraphText()).toContain(
      "Join us now and start skydiving for just £175!"
    );
  });

  it("should have the correct title", async () => {
    await page.navigateTo();
    expect(await page.getTitle()).toEqual("Leeds University Skydivers");
  });

  it("should show an error message when navigating to a page that does not exist", async () => {
    await page.navigateToUnknownPage();
    expect(await page.getParagraphText()).toContain("404 - Page not found");
  });
});
