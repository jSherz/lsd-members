/**
  * MIT License
  *
  * Copyright (c) 2016 James Sherwood-Jones <james.sherwoodjones@gmail.com>
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all
  * copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  * SOFTWARE.
  */

package helpers

import java.time.DayOfWeek

import org.joda.time.{DateTime, DateTimeConstants, Period}

import scala.collection.GenTraversableOnce
import scala.collection.immutable.IndexedSeq

/**
  * Functions used in the course calendar view.
  */
object CalendarHelper {
  /**
    * If a calendar shows a month starting on a Monday, how many days should it show from the previous month?
    */
  private val previousMonthDaysForMonday = 7

  /**
    * The number of tiles that are shown on each calendar.
    */
  private val tilesPerCalendar = 42

  /**
    * One tile on a calendar.
    *
    * @param date The underlying date
    * @param isPreviousMonth Is this tile in the month shown before the primary month?
    * @param isCurrentMonth Is this tile part of the primary month being displayed?
    * @param isNextMonth Is this tile in the month shown after the primary month?
    * @param isToday Does this tile represent today?
    */
  case class CalendarTile(date: DateTime, isPreviousMonth: Boolean, isCurrentMonth: Boolean, isNextMonth: Boolean, isToday: Boolean)

  /**
    * Gets information about the calendar tile with the given month being displayed.
    *
    * A calendar is a 6x7 grid, starting at index 0 in the top left corner:
    *
    * - - - - - - -
    * - - - - - - -
    * - - - - - - -
    * - - - - - - -
    * - - - - - - -
    * - - - - - - -
    *
    * The month to be displayed is primarily shown on the centre area, with the previous and next months surrounding it.
    *
    * @param month The primary month being shown
    * @param currentDay Today
    * @return 42 calendar tiles
    */
  def getTiles(month: DateTime, currentDay: DateTime): Seq[CalendarTile] = {
    val firstDay = firstDayOfMonth(month)

    // Calculate how many days to show of the previous month
    val daysOfPrevMonth = numDaysOfPreviousMonth(firstDay.getDayOfWeek)
    val daysOfPrimaryMonth = firstDay.dayOfMonth().getMaximumValue
    val daysOfLastMonth = tilesPerCalendar - daysOfPrimaryMonth - daysOfPrevMonth

    val nextMonth = firstDay.plus(Period.months(1))

    Seq(
      (1 to daysOfPrevMonth).map { offset => firstDay.minus(Period.days(offset)) }.reverse,
      (1 to daysOfPrimaryMonth).map { offset => firstDay.plus(Period.days(offset - 1)) },
      (1 to daysOfLastMonth).map { offset => nextMonth.plus(Period.days(offset - 1)) }
    ).flatMap { days => toTiles(firstDay, nextMonth, currentDay, days) }
  }

  /**
    * How many days of the previous month should be shown based on which day of the week the current month starts with.
    *
    * For example, if the current month starts with Monday (7) show 7 days of the last month.
    *
    * @param dayOfWeek 1-7, represents Monday to Sunday
    * @return Number of days to show from previous month
    */
  private def numDaysOfPreviousMonth(dayOfWeek: Int): Int = {
    dayOfWeek match {
      case DateTimeConstants.MONDAY => previousMonthDaysForMonday
      case _ => dayOfWeek - 1
    }
  }

  /**
    * Get the first day of a date in a particular month.
    *
    * @param month to find the first day of
    * @return date with the same year, month but with the 1st day at 00:00
    */
  private def firstDayOfMonth(month: DateTime): DateTime = {
    new DateTime(month.getYear, month.getMonthOfYear, 1, 0, 0, month.getChronology)
  }

  /**
    * Takes a set of dates on a calendar and turns it into tiles.
    *
    * @param month First day of the month being shown
    * @param nextMonth First day of the next month
    * @param today Today
    * @param inputDates The date that will be shown on the tile
    * @return
    */
  private def toTiles(month: DateTime, nextMonth: DateTime, today: DateTime, inputDates: Seq[DateTime]): Seq[CalendarTile] = {
    inputDates.map { inputDate =>
      val isPreviousMonth = inputDate.isBefore(month)
      val isCurrentMonth = false
      val isNextMonth = inputDate.isEqual(nextMonth) || inputDate.isAfter(nextMonth)
      val isToday = inputDate.toLocalDate == today.toLocalDate

      CalendarTile(inputDate, isPreviousMonth, isCurrentMonth, isNextMonth, isToday)
    }
  }
}
