import {Component, OnInit, OnDestroy} from '@angular/core';
import {Subscription} from 'rxjs';
import {ActivatedRoute} from '@angular/router';

import {MemberService, Member, TextMessage} from '../member.service';

@Component({
  selector: 'lsd-member-view',
  templateUrl: './member-view.component.html',
  styleUrls: ['./member-view.component.sass']
})
export class MemberViewComponent implements OnInit, OnDestroy {

  private displayMemberSub: Subscription;

  private member: Member;

  private textMessages: TextMessage[] = [];

  constructor(private service: MemberService,
              private route: ActivatedRoute) {
  }

  showMember(uuid: string) {
    this.service.getMember(uuid).subscribe(member => this.member = member);

    this.service.getTextMessages(uuid).subscribe(textMessages => this.textMessages = textMessages);
  }

  ngOnInit() {
    this.displayMemberSub = this.route.params
      .subscribe(params => {
        let uuid: string = params['uuid'];

        this.showMember(uuid);
      });
  }

  ngOnDestroy() {
    this.displayMemberSub.unsubscribe();
  }

  parseStatus(statusId: number) {
    switch (statusId) {
      case 0: return 'Pending';
      case 1: return 'Sent';
      case 2: return 'Error';
      case 3: return 'Received';
      default: return 'Unknown (' + statusId + ')';
    }
  }

}
