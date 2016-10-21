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
  selector: 'lsd-committee',
  templateUrl: 'committee.component.html',
  styleUrls: ['committee.component.sass']
})
export class CommitteeComponent implements OnInit {

  committeeMembers = [
    new CommitteeMember('emily.jpg', 'Emily', 'President'),
    new CommitteeMember('will.jpg', 'Will', 'Vice-president'),
    new CommitteeMember('angus.jpg', 'Angus', 'Treasurer'),
    new CommitteeMember('jim.jpg', 'Jim', 'RAPS Secretary'),
    new CommitteeMember('nathan.jpg', 'Nathan', 'Kit Secretary'),
    new CommitteeMember('isabelle.jpg', 'Isabelle', 'Social Secretary #1'),
    new CommitteeMember('georgia.jpg', 'Georgia', 'Social Secretary #2'),
    new CommitteeMember('james.jpg', 'James', '"The website guy"'),
  ];

  constructor() { }

  ngOnInit() {
  }

}
