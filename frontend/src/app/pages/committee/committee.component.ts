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
    new CommitteeMember('emily.png', 'Emily', 'President'),
    new CommitteeMember('will.png', 'Will', 'Vice-president'),
    new CommitteeMember('angus.png', 'Angus', 'Treasurer'),
    new CommitteeMember('jim.jpg', 'Jim', 'RAPS Secretary'),
    new CommitteeMember('nathan.png', 'Nathan', 'Kit Secretary'),
    new CommitteeMember('isabelle.png', 'Isabelle', 'Social Secretary #1'),
    new CommitteeMember('georgia.png', 'Georgia', 'Social Secretary #2'),
    new CommitteeMember('james.png', 'James', '"The website guy"'),
  ];

}
