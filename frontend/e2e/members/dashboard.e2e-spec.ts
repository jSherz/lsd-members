import { DashboardPage } from "./dashboard-page.po";

describe("Members Dashboard", function () {
  let page: DashboardPage;
  let baseUrl: string;

  beforeEach(() => {
    page = new DashboardPage();
    baseUrl = page.baseUrl();

    // Mmmm hacky
    if (!baseUrl.endsWith("/")) {
      baseUrl += "/";
    }
  });

  it("will navigate a user that's not logged in to the login page", async () => {
    await page.navigateTo(null, null);

    await page.waitForMemberLogin();
    expect(await page.getCurrentUrl()).toEqual(baseUrl + "members");
  });

  it("will clear the JWT of a user if it is no longer valid", async () => {
    await page.navigateTo("this-is-an-invalid-jwt", false);

    expect(await page.getCurrentUrl()).toEqual(baseUrl + "members/dashboard");

    await page.rawNavigateTo();

    await page.waitForMemberLogin();
    expect(await page.getCurrentUrl()).toEqual(baseUrl + "members");
  });
});
