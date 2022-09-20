import { HomePage } from "./home.po";

describe("Pages: Home", function () {
  let page: HomePage;
  let baseUrl: string;

  beforeEach(() => {
    page = new HomePage();
    baseUrl = page.baseUrl();
  });

  it("should display the correct banner", async () => {
    await page.navigateTo();
    expect(await page.getIntroSnippet()).toContain(
      "Join us now and start skydiving for just £195!"
    );
  });

  it("should highlight only the home button", async () => {
    await page.navigateTo();
    expect(await page.getHomeLink().getAttribute("class")).toContain("active");

    for (const pageLink of [
      page.getAboutLink(),
      page.getCommitteeLink(),
      page.getPricesLink(),
      page.getFaqLink(),
      page.getJoinLink(),
      page.getContactLink(),
    ]) {
      expect(await pageLink.getAttribute("class")).not.toContain("active");
    }
  });

  it("should not navigate anywhere when home is clicked", async () => {
    await page.navigateTo();
    await page.getHomeLink().click();

    expect(await page.getCurrentUrl()).toEqual(baseUrl);
  });

  it("should navigate to the correct page when about is clicked", async () => {
    await page.navigateTo();
    await page.getAboutLink().click();

    expect(await page.getCurrentUrl()).toEqual(baseUrl + "about-the-club");
  });

  it("should navigate to the correct page when committee is clicked", async () => {
    await page.navigateTo();
    await page.getCommitteeLink().click();

    expect(await page.getCurrentUrl()).toEqual(baseUrl + "committee");
  });

  it("should navigate to the correct page when prices is clicked", async () => {
    await page.navigateTo();
    await page.getPricesLink().click();

    expect(await page.getCurrentUrl()).toEqual(baseUrl + "prices");
  });

  it("should navigate to the correct page when FAQ is clicked", async () => {
    await page.navigateTo();
    await page.getFaqLink().click();

    expect(await page.getCurrentUrl()).toEqual(baseUrl + "faq");
  });

  it("should have the correct link for joining", async () => {
    await page.navigateTo();
    expect(await page.getJoinLink().getAttribute("href")).toEqual(
      "https://www.luu.org.uk/skydiving/"
    );
  });

  it("should navigate to the correct page when contact is clicked", async () => {
    await page.navigateTo();
    await page.getContactLink().click();

    expect(await page.getCurrentUrl()).toEqual(baseUrl + "contact");
  });
});
