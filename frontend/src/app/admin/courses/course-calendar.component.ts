import { Component, OnInit, OnDestroy }      from '@angular/core';
import { ROUTER_DIRECTIVES, ActivatedRoute } from '@angular/router';
import { Subscription }                      from 'rxjs/Subscription';
import * as moment                           from 'moment';
import { MonthService }                      from './month.service';
import { Tile, TileService }                 from './tile.service';
import { TileComponent }                     from './tile.component';

@Component({
  moduleId: module.id,
  selector: 'div',
  templateUrl: 'course-calendar.component.html',
  providers: [MonthService, TileService],
  directives: [ROUTER_DIRECTIVES, TileComponent]
})
export class CourseCalendarComponent implements OnInit, OnDestroy {

  private displayMonth = moment();

  private displayMonthSub: Subscription;

  months: moment.Moment[];
  currentMonth: moment.Moment;
  previousMonth: moment.Moment;
  nextMonth: moment.Moment;
  tiles: Tile[];

  constructor(
    private monthService: MonthService,
    private route: ActivatedRoute,
    private tileService: TileService) { }

  private updateCalendar() {
    this.months = this.monthService.get(this.displayMonth);

    this.currentMonth = moment([this.displayMonth.year(), this.displayMonth.month(), 1]);

    this.previousMonth = this.currentMonth.clone().subtract(1, 'months');
    this.nextMonth = this.currentMonth.clone().add(1, 'months');

    this.tiles = this.tileService.getTiles(this.currentMonth, moment());
  }

  ngOnInit() {
    this.displayMonthSub = this.route.params
      .subscribe(params => {
        let year: number = +params['year'];
        let month: number = +params['month'];

        if (!isNaN(year) && !isNaN(month)) {
          // Convert 1 indexed month to 0 indexed for momentjs / JS
          let zeroIndexedMonth: number = month - 1;
          this.displayMonth = moment([year, zeroIndexedMonth, 1]);
          this.updateCalendar();
        }
      });

    this.updateCalendar();
  }

  ngOnDestroy() {
    this.displayMonthSub.unsubscribe();
  }

}
