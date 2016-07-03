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

package com.jsherz.luskydive.dao

import com.jsherz.luskydive.models.Setting
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Used to access settings stored in the database.
  */
class SettingsDAO (override protected val dbConfig: DatabaseConfig[JdbcProfile]) extends Tables(dbConfig) {
  import driver.api._

  /**
    * Check if a setting exists in the database.
    *
    * @param key Used to find the setting
    * @return
    */
  def exists(key: String): Future[Boolean] = db.run(Settings.filter(_.key === key).exists.result)

  /**
    * Get a setting from the database.
    *
    * @param key Key to find a setting with
    * @return
    */
  def get(key: String): Future[Option[Setting]] = db.run(Settings.filter(_.key === key).result.headOption)

  /**
    * Get the value of the setting with the given key. If not found, return the specified default value.
    *
    * @param key Key to find a setting with
    * @param defaultValue Value returned if the setting is not found
    * @return
    */
  def getOrElse(key: String, defaultValue: String): Future[String] = {
    get(key).map {
      case Some(setting) => setting.value
      case _ => defaultValue
    }
  }

  /**
    * Sets the value of a setting with the given key. If it does not exist, it will be created. Otherwise, the value
    * will be updated.
    *
    * @param setting Setting to insert or update
    * @return The number of settings that were updated (always one)
    */
  def put(setting: Setting): Future[Int] = db.run(Settings.insertOrUpdate(setting))

  /**
    * Remove a setting from the database.
    *
    * @param setting Setting to remove
    * @return The number of settings that were removed (always one)
    */
  def delete(setting: Setting): Future[Int] = db.run(Settings.filter(_.key === setting.key).delete)

  /**
    * Remove all settings
    *
    * @return The number of settings that were removed
    */
  def empty(): Future[Int] = db.run(Settings.delete)
}
