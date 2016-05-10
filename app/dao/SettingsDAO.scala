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

package dao

import javax.inject.Inject

import models.Setting
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Used to access settings stored in the database.
  */
class SettingsDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val Settings = TableQuery[SettingsTable]

  /**
    * Get all of the configured settings.
    *
    * @return
    */
  def all(): Future[Seq[Setting]] = db.run(Settings.result)

  /**
    * Get a setting from the database.
    *
    * @param key
    * @return
    */
  def get(key: String): Future[Option[Setting]] = db.run(Settings.filter(_.key === key).result.headOption)

  /**
    * The Slick mapping for the settings table.
    *
    * @param tag
    */
  private class SettingsTable(tag: Tag) extends Table[Setting](tag, "settings") {

    def key: Rep[String] = column[String]("key", O.PrimaryKey)

    def value: Rep[String] = column[String]("value")

    def * = (key, value) <> (Setting.tupled, Setting.unapply)

  }

}
