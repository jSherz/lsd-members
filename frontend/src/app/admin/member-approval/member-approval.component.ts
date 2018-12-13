import { Component, OnInit } from "@angular/core";

import { Member } from "../member/model/member";

@Component({
  selector: "lsd-member-approval",
  templateUrl: "./member-approval.component.html",
  styleUrls: ["./member-approval.component.sass"]
})
export class MemberApprovalComponent implements OnInit {
  exampleMembers: any[] = [
    { id: 1 },
    { id: 2 },
    { id: 3 },
    { id: 4 },
    { id: 5 }
  ];

  constructor() {}

  ngOnInit() {}

  approveMember(member: Member) {
    alert("Approving " + JSON.stringify(member));
  }

  rejectMember(member: Member) {
    alert("Rejecting " + JSON.stringify(member));
  }

  deleteRejection(member: Member) {
    alert("Deleting " + JSON.stringify(member));
  }
}
