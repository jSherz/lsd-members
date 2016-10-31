import {browser, element, by} from 'protractor';

export class PagesPage {

  getCurrentUrl() {
    return browser.getCurrentUrl();
  }

  getLinks() {
    return element.all(by.css('lsd-page-nav-item a'));
  }

  getHomeLink() {
    return this.getLinks().get(0);
  }

  getAboutLink() {
    return this.getLinks().get(1);
  }

  getCommitteeLink() {
    return this.getLinks().get(2);
  }

  getPricesLink() {
    return this.getLinks().get(3);
  }

  getFaqLink() {
    return this.getLinks().get(4);
  }

  getJoinLink() {
    return element(by.css('#join-lsd-btn a'));
  }

  getContactLink() {
    return this.getLinks().get(5);
  }

}
