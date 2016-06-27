import { LuskydivePage } from './app.po';

describe('luskydive App', function() {
  let page: LuskydivePage;

  beforeEach(() => {
    page = new LuskydivePage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
