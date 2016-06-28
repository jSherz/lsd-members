export class LuskydivePage {
  navigateTo() {
    return browser.get('/');
  }

  getParagraphText() {
    return element(by.css('app-root')).getText();
  }

  getTitle() {
    return element(by.css('title')).getInnerHtml();
  }
}
