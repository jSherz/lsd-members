import {browser, element, by} from 'protractor';

export class MembersLoginPage {

  navigateTo() {
    return browser.get('/members');
  }

  getCurrentUrl() {
    return browser.getCurrentUrl();
  }

  loginButton() {
    return element(by.css('#login-button a'));
  }

  loginIntroText() {
    return element(by.css('#login:nth-child(1) p'));
  }

  fbUsernameBox() {
    return element(by.css('#email'));
  }

  fbPasswordBox() {
    return element(by.css('#pass'));
  }

  fbLoginButton() {
    return element(by.css('#loginbutton'));
  }

  profile() {
    return element(by.css('#profile'));
  }

  syncOff() {
    return browser.waitForAngularEnabled(false);
  }

  syncOn() {
    return browser.waitForAngularEnabled(true);
  }

  waitForDashboard() {
    return browser.wait(browser.ExpectedConditions.urlContains('members/dashboard'), 5000);
  }

  baseUrl() {
    return browser.baseUrl;
  }

}
