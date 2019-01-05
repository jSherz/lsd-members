import { AboutPage } from "./about.po";

describe("Pages: About", function() {
  let page: AboutPage;

  beforeEach(() => {
    page = new AboutPage();
  });

  it("should have the correct link to join", () => {
    page.navigateTo();
    expect(page.getJoinButton().getAttribute("href")).toEqual(
      "http://www.luu.org.uk/skydiving/"
    );
  });

  it("should have the correct affiliate links", () => {
    page.navigateTo();

    expect(
      page
        .getAffiliateLinks()
        .get(0)
        .getAttribute("href")
    ).toEqual("http://www.flyaerodyne.com/");
    expect(
      page
        .getAffiliateLinks()
        .get(1)
        .getAttribute("href")
    ).toEqual("http://www.bpa.org.uk/");
    expect(
      page
        .getAffiliateLinks()
        .get(2)
        .getAttribute("href")
    ).toEqual("https://www.bcpa.org.uk/");
  });

  it("should highlight only the about button", () => {
    page.navigateTo();
    expect(page.getAboutLink().getAttribute("class")).toContain("active");

    [
      page.getHomeLink(),
      page.getCommitteeLink(),
      page.getPricesLink(),
      page.getFaqLink(),
      page.getJoinLink(),
      page.getContactLink()
    ].forEach(pageLink =>
      expect(pageLink.getAttribute("class")).not.toEqual("active")
    );
  });
});
