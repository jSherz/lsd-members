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
    new CommitteeMember('amber.jpg', 'Amber', 'President'),
    new CommitteeMember('ruby.jpg', 'Ruby', 'Vice-President'),
    new CommitteeMember('will.jpg', 'Will', 'RAPS Secretary'),
    new CommitteeMember('sean.jpg', 'Sean', 'Treasurer'),
    new CommitteeMember('tom.jpg', 'Tom', 'Social Secretary'),
    new CommitteeMember('tom2.jpg', 'Tom', 'Social Secretary'),
    new CommitteeMember('emma.jpg', 'Emma', 'Kit Secretary'),
  ];

  constructor() { }

  ngOnInit() {
  }

}
