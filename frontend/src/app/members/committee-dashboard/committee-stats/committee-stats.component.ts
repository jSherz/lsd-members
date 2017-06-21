import {Component, OnInit} from '@angular/core';

import {CommitteeStatsService} from './committee-stats.service';

@Component({
  selector: 'lsd-committee-stats',
  templateUrl: './committee-stats.component.html',
  styleUrls: ['./committee-stats.component.sass']
})
export class CommitteeStatsComponent implements OnInit {

  numReceived?: number = null;

  constructor(private service: CommitteeStatsService) {
  }

  ngOnInit() {
    this.service.getNumReceivedMessages()
      .subscribe((numReceived) => this.numReceived = numReceived,
        (error) => {
          console.error('Failed to get number of received messages.', error);
        });
  }

}
