import { browser, element, by } from "protractor";

export class LuskydivePage {
  navigateTo() {
    return browser.get("/");
  }

  navigateToUnknownPage() {
    return browser.get("/asdfasdfasdfasdf");
  }

  getParagraphText() {
    return element(by.css("lsd-root")).getText();
  }

  getTitle() {
    return element(by.css("title")).getAttribute("innerHTML");
  }
}
