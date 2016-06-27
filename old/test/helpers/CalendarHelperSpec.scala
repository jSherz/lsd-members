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

import java.io._

import helpers.CalendarHelper
import helpers.CalendarHelper.CalendarTile
import org.joda.time.chrono.ISOChronology
import org.joda.time.{DateTime, DateTimeZone, Period}

/**
  * Tests the helper used to create calendar tiles.
  */
class CalendarHelperSpec extends BaseSpec {
  val tz = ISOChronology.getInstance(DateTimeZone.UTC)

  "CalendarHelper" should {
    "return the correct tiles for months starting in each day of the week" in {
      val fixtureReader = new ObjectInputStream(new FileInputStream("test/fixtures/calendar_tiles.dat"))
      val fixtureTiles = fixtureReader.readObject().asInstanceOf[Map[DateTime, Seq[CalendarTile]]]
      fixtureReader.close

      for ((month, correctTiles) <- fixtureTiles) {
        val testCurrentDay = month.plus(Period.days(3))
        val generatedTiles = CalendarHelper.getTiles(month, testCurrentDay)

        generatedTiles mustEqual correctTiles
      }
    }
  }
}
