import { AboutPage } from "./about.po";

describe("Pages: About", function () {
  let page: AboutPage;

  beforeEach(() => {
    page = new AboutPage();
  });

  it("should have the correct link to join", async () => {
    await page.navigateTo();
    expect(await page.getJoinButton().getAttribute("href")).toEqual(
      "http://www.luu.org.uk/skydiving/"
    );
  });

  it("should have the correct affiliate links", async () => {
    await page.navigateTo();

    expect(await page.getAffiliateLinks().get(0).getAttribute("href")).toEqual(
      "http://www.flyaerodyne.com/"
    );
    expect(await page.getAffiliateLinks().get(1).getAttribute("href")).toEqual(
      "http://www.bpa.org.uk/"
    );
    expect(await page.getAffiliateLinks().get(2).getAttribute("href")).toEqual(
      "https://www.bcpa.org.uk/"
    );
  });

  it("should highlight only the about button", async () => {
    await page.navigateTo();
    expect(await page.getAboutLink().getAttribute("class")).toContain("active");

    for (const pageLink of [
      page.getHomeLink(),
      page.getCommitteeLink(),
      page.getPricesLink(),
      page.getFaqLink(),
      page.getJoinLink(),
      page.getContactLink(),
    ]) {
      expect(await pageLink.getAttribute("class")).not.toEqual("active");
    }
  });
});
