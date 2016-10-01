import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Member} from '../../index';

/**
 * This is a page that shows the MemberSearchComponent and allows a user to look for and then view a member.
 */
@Component({
  selector: 'app-member-lookup',
  templateUrl: './member-lookup.component.html',
  styleUrls: ['./member-lookup.component.sass']
})
export class MemberLookupComponent {

  constructor(private router: Router) {
  }

  viewMember(member: Member) {
    this.router.navigate(['/admin', 'members', member.uuid]);
  }

}
