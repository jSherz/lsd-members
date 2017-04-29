import {CommitteePage} from './committee.po';


describe('Pages: Committee', function () {

  let page: CommitteePage;

  beforeEach(() => {
    page = new CommitteePage();
  });

  it('should show the correct committee information', () => {
    page.navigateTo();

    const imageUrls = page.committeeList().map(listItem => listItem.$('img').getAttribute('src'));
    const imageTitles = page.committeeList().map(listItem => listItem.$('img').getAttribute('title'));
    const imageAlts = page.committeeList().map(listItem => listItem.$('img').getAttribute('alt'));
    const headers = page.committeeList().map(listItem => listItem.$('h3').getText());
    const roles = page.committeeList().map(listItem => listItem.$('p').getText());

    const baseUrl = 'http://localhost:49152/assets/images/committee/';

    expect(imageUrls).toEqual([
      baseUrl + 'emily.jpg', baseUrl + 'will.jpg', baseUrl + 'angus.jpg', baseUrl + 'jim.jpg', baseUrl + 'nathan.jpg',
      baseUrl + 'isabelle.jpg', baseUrl + 'georgia.jpg', baseUrl + 'james.jpg'
    ]);

    const names = ['Emily', 'Will', 'Angus', 'Jim', 'Nathan', 'Isabelle', 'Georgia', 'James'];

    expect(imageTitles).toEqual(names);
    expect(imageAlts).toEqual(names);

    expect(headers).toEqual(names);

    expect(roles).toEqual([
      'President', 'Vice-president', 'Treasurer', 'RAPS Secretary', 'Kit Secretary', 'Social Secretary #1',
      'Social Secretary #2', '"The website guy"'
    ]);
  });

});
