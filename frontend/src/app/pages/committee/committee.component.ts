import { Component, OnInit } from '@angular/core';

export class CommitteeMember {
  imageUrl: string;
  name: string;
  role: string;

  constructor(imageUrl: string, name: string, role: string) {
    this.imageUrl = imageUrl;
    this.name = name;
    this.role = role;
  }
}

@Component({
  selector: 'app-committee',
  templateUrl: 'committee.component.html',
  styleUrls: ['committee.component.sass']
})
export class CommitteeComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  committeeMembers = [
    new CommitteeMember('aucutt.jpg', 'Emily', 'Party Sec'),
    new CommitteeMember('dickinson.jpg', 'Jim', 'Pineapple Sec'),
    new CommitteeMember('mansfield.jpg', 'Becca', 'Captain'),
    new CommitteeMember('priestley.jpg', 'Tyler', 'Triple Sec'),
    new CommitteeMember('ravet_bailey.jpg', 'Tom', 'French Sec'),
    new CommitteeMember('walkerj.jpg', 'Jack', 'Party Sec'),
    new CommitteeMember('walker.jpg', 'Bethan', 'Party Sec'),
    new CommitteeMember('sherwood_jones.jpg', 'James', 'Creator of Bugs'),
  ];

}
