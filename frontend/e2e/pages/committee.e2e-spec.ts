import {CommitteePage} from './committee.po';

describe('Pages: Committee', function () {

  let page: CommitteePage;
  let baseUrl: string;

  beforeEach(() => {
    page = new CommitteePage();
    baseUrl = page.baseUrl();
  });

  it('should show the correct committee information', () => {
    page.navigateTo();

    const imageUrls = page.committeeList().map(listItem => listItem.$('img').getAttribute('src'));
    const imageTitles = page.committeeList().map(listItem => listItem.$('img').getAttribute('title'));
    const imageAlts = page.committeeList().map(listItem => listItem.$('img').getAttribute('alt'));
    const headers = page.committeeList().map(listItem => listItem.$('h3').getText());
    const roles = page.committeeList().map(listItem => listItem.$('p').getText());

    const assetUrl = baseUrl + 'assets/images/committee/';

    expect(imageUrls).toEqual([
      assetUrl + 'emily.jpg', assetUrl + 'will.jpg', assetUrl + 'angus.jpg', assetUrl + 'jim.jpg',
      assetUrl + 'nathan.jpg', assetUrl + 'georgia.jpg'
    ]);

    const names = ['Emily', 'Will', 'Angus', 'Jim', 'Nathan', 'Georgia'];

    expect(imageTitles).toEqual(names);
    expect(imageAlts).toEqual(names);

    expect(headers).toEqual(names);

    expect(roles).toEqual([
      'President', 'Vice-president', 'Treasurer', 'RAPS Secretary', 'Kit Secretary', 'Social Secretary'
    ]);
  });

});
