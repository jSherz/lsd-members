import { browser, element, by } from "protractor";
import { PagesPage } from "./pages.po";

export class AboutPage extends PagesPage {
  navigateTo() {
    return browser.get("/about-the-club");
  }

  getJoinButton() {
    return browser.element(by.css("#join-button"));
  }

  getAffiliateLinks() {
    return browser.$$("#affiliates li a");
  }
}
