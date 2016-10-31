import {LuskydivePage} from './app.po';

describe('LSD App', function () {

  let page: LuskydivePage;

  beforeEach(() => {
    page = new LuskydivePage();
  });

  it('should display the main form by default', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toContain('Join us now and start skydiving for just £175!');
  });

  it('should have the correct title', () => {
    page.navigateTo();
    expect(page.getTitle()).toEqual('Leeds University Skydivers');
  });

});
