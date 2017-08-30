import {browser, element, by, ExpectedConditions} from 'protractor';

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
    return element(by.css('#login p:nth-child(1)'));
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

  waitForFacebookLoginPage() {
    return browser.wait(ExpectedConditions.and(
      ExpectedConditions.urlContains('login.php'),
      ExpectedConditions.presenceOf(this.fbUsernameBox())
    ), 5000);
  }

  waitForAppPage() {
    return browser.wait(ExpectedConditions.urlContains('members'), 5000);
  }

  waitForDashboard() {
    return browser.wait(ExpectedConditions.urlContains('members/dashboard'), 5000);
  }

  baseUrl() {
    return browser.baseUrl;
  }

}
