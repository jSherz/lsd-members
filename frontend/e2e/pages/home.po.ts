import { browser, element, by } from "protractor";
import { PagesPage } from "./pages.po";

export class HomePage extends PagesPage {
  navigateTo() {
    return browser.get("/");
  }

  getIntroSnippet() {
    return element(by.css("#intro-snippet")).getText();
  }

  baseUrl() {
    return browser.baseUrl;
  }
}
