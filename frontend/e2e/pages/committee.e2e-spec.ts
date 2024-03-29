import { CommitteePage } from "./committee.po";

describe("Pages: Committee", function () {
  let page: CommitteePage;
  let baseUrl: string;

  beforeEach(() => {
    page = new CommitteePage();
    baseUrl = page.baseUrl();
  });

  it("should show the correct committee information", async () => {
    await page.navigateTo();

    const imageUrls = await page
      .committeeList()
      .map((listItem) => listItem.$("img").getAttribute("src"));
    const imageTitles = await page
      .committeeList()
      .map((listItem) => listItem.$("img").getAttribute("title"));
    const imageAlts = await page
      .committeeList()
      .map((listItem) => listItem.$("img").getAttribute("alt"));
    const headers = await page
      .committeeList()
      .map((listItem) => listItem.$("h3").getText());
    const roles = await page
      .committeeList()
      .map((listItem) => listItem.$("p").getText());

    const assetUrl = baseUrl + "assets/images/committee/";

    expect(imageUrls).toEqual([
      assetUrl + "amber.jpg",
      assetUrl + "ruby.jpg",
      assetUrl + "will.jpg",
      assetUrl + "sean.jpg",
      assetUrl + "tom.jpg",
      assetUrl + "tom2.jpg",
      assetUrl + "emma.jpg",
    ]);

    const names = ["Padders", "Ruby", "Will", "Sean", "Tom", "Tom", "Emma"];

    expect(imageTitles).toEqual(names);
    expect(imageAlts).toEqual(names);

    expect(headers).toEqual(names);

    expect(roles).toEqual([
      "President",
      "Vice-President",
      "RAPS Secretary",
      "Treasurer",
      "Social Secretary",
      "Social Secretary",
      "Kit Secretary",
    ]);
  });
});
