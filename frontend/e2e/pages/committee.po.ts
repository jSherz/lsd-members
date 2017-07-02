import {browser, element, by} from 'protractor';
import {PagesPage} from './pages.po';


export class CommitteePage extends PagesPage {

  navigateTo() {
    return browser.get('/committee');
  }

  committeeList() {
    return browser.$$('#committee-list li');
  }

  baseUrl() {
    return browser.baseUrl;
  }

}
