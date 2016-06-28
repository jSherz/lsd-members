import { LuskydivePage } from './app.po';

describe('luskydive App', function() {
  let page: LuskydivePage;

  beforeEach(() => {
    page = new LuskydivePage();
  });

  it('should display the main form by default', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toContain('Name');
    expect(page.getParagraphText()).toContain('Phone number');
  });

  it('should have the correct title', () => {
    page.navigateTo();
    expect(page.getTitle()).toEqual('Sign-up - Leeds University Skydivers');
  });
});
