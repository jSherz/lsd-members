export class LuskydivePage {
  navigateTo() {
    return browser.get('/');
  }

  getParagraphText() {
    return element(by.css('lsd-root')).getText();
  }

  getTitle() {
    return element(by.css('title')).getInnerHtml();
  }
}
