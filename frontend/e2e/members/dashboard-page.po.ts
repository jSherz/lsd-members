import {browser, element, by, ExpectedConditions} from 'protractor';

export class DashboardPage {

  navigateTo(jwt: string, committee: boolean) {
    return Promise.all([
      browser.get('/'),
      this.setLocalStorage('IS_COMMITTEE', committee),
      this.setLocalStorage('JWT', jwt),
      this.rawNavigateTo()
    ]);
  }

  /**
   * Navigate without setting up local storage.
   */
  rawNavigateTo() {
    return browser.get('/members/dashboard');
  }

  getCurrentUrl() {
    return browser.getCurrentUrl();
  }

  baseUrl() {
    return browser.baseUrl;
  }

  refresh() {
    return browser.refresh();
  }

  waitForMemberLogin() {
    return browser.wait(ExpectedConditions.not(ExpectedConditions.urlContains('members/dashboard')));
  }

  private setLocalStorage(key: string, value: any) {
    return value === null ?
      browser.executeScript(`window.localStorage.removeItem("${key}")`) :
      browser.executeScript(`window.localStorage.setItem("${key}", ${JSON.stringify(value)})`);
  }

}
