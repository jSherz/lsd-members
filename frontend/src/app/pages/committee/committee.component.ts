import { Component } from "@angular/core";

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
  selector: "lsd-committee",
  templateUrl: "committee.component.html",
  styleUrls: ["committee.component.sass"]
})
export class CommitteeComponent {
  committeeMembers = [
    new CommitteeMember("daisy.jpg", "Daisy-May", "President"),
    new CommitteeMember("zuzanna.jpg", "Zuzanna", "Secretary"),
    new CommitteeMember("ben.jpg", "Ben", "Treasurer"),
    new CommitteeMember("maurice.jpg", "Maurice", "Kit & Social Sec."),
    new CommitteeMember("sivan.jpg", "Sivan", "Social Media Sec."),
  ];
}
