import {Component, OnInit, OnDestroy} from '@angular/core';
import {Subscription} from 'rxjs';
import {ActivatedRoute} from '@angular/router';
import * as moment from 'moment';

import {MemberViewService, Member, TextMessage} from './member-view.service';

@Component({
  selector: 'app-member-view',
  templateUrl: './member-view.component.html',
  styleUrls: ['./member-view.component.sass']
})
export class MemberViewComponent implements OnInit, OnDestroy {

  private displayMemberSub: Subscription;

  private member: Member;

  private textMessages: TextMessage[] = [];

  constructor(private service: MemberViewService,
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
      default: return 'Unknown (' + statusId + ')';
    }
  }

}