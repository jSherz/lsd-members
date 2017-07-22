import {Component, OnInit} from '@angular/core';

import {DashboardStatsService} from './dashboard-stats.service';

@Component({
  selector: 'lsd-committee-stats',
  templateUrl: './dashboard-stats.component.html',
  styleUrls: ['./dashboard-stats.component.sass']
})
export class DashboardStatsComponent implements OnInit {

  numReceived?: number = null;

  constructor(private service: DashboardStatsService) {
  }

  ngOnInit() {
    this.service.getNumReceivedMessages()
      .subscribe((numReceived) => this.numReceived = numReceived,
        (error) => {
          console.error('Failed to get number of received messages.', error);
        });
  }

}
