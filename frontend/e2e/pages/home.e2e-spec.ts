import {HomePage} from './home.po';

describe('Pages: Home', function () {

  let page: HomePage;
  let baseUrl: string;

  beforeEach(() => {
    page = new HomePage();
    baseUrl = page.baseUrl();
  });

  it('should display the correct banner', () => {
    page.navigateTo();
    expect(page.getIntroSnippet()).toContain('Join us now and start skydiving for just £175!');
  });

  it('should highlight only the home button', () => {
    page.navigateTo();
    expect(page.getHomeLink().getCssValue('background-color')).toEqual('rgba(217, 244, 255, 1)');

    [
      page.getAboutLink(),
      page.getCommitteeLink(),
      page.getPricesLink(),
      page.getFaqLink(),
      page.getJoinLink(),
      page.getContactLink()
    ].forEach(pageLink => expect(pageLink.getCssValue('background-color')).toEqual('rgba(0, 0, 0, 0)'));
  });

  it('should not navigate anywhere when home is clicked', () => {
    page.navigateTo();
    page.getHomeLink().click();

    expect(page.getCurrentUrl()).toEqual(baseUrl);
  });

  it('should navigate to the correct page when about is clicked', () => {
    page.navigateTo();
    page.getAboutLink().click();

    expect(page.getCurrentUrl()).toEqual(baseUrl + 'about-the-club');
  });

  it('should navigate to the correct page when committee is clicked', () => {
    page.navigateTo();
    page.getCommitteeLink().click();

    expect(page.getCurrentUrl()).toEqual(baseUrl + 'committee');
  });

  it('should navigate to the correct page when prices is clicked', () => {
    page.navigateTo();
    page.getPricesLink().click();

    expect(page.getCurrentUrl()).toEqual(baseUrl + 'prices');
  });

  it('should navigate to the correct page when FAQ is clicked', () => {
    page.navigateTo();
    page.getFaqLink().click();

    expect(page.getCurrentUrl()).toEqual(baseUrl + 'faq');
  });

  it('should have the correct link for joining', () => {
    page.navigateTo();
    expect(page.getJoinLink().getAttribute('href')).toEqual('https://www.luu.org.uk/groups/skydiving/');
  });

  it('should navigate to the correct page when contact is clicked', () => {
    page.navigateTo();
    page.getContactLink().click();

    expect(page.getCurrentUrl()).toEqual(baseUrl + 'contact');
  });

});
