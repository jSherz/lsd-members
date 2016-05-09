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

import dao.MemberDAO
import play.api.Application
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.guice.GuiceApplicationBuilder

/**
 * Useful helper functions for testing the app.
 */
object Helpers {
  /**
   * Tells play-slick to use a particular db configuration. For example, with:
   *
   * slick {
   *   dbs {
   *     default {
   *       driver = "slick.driver.PostgresDriver$"
   * 
   *       db {
   *         driver = "org.postgresql.Driver"
   *         dataSourceClass = "slick.jdbc.DatabaseUrlDataSource"
   *         connectionTestQuery = "SELECT 1"
   *       }
   *     }
   * 
   *     test {
   *       driver = "slick.driver.H2Driver$"
   * 
   *       db {
   *         driver = "org.h2.Driver"
   *         url = "jdbc:h2:mem:play"
   *       }
   *     }
   *   }
   * }
   *
   * withSlickDb("test") would use the second configuration (H2) and withSlickDb("default") would use the first config
   */
  def withSlickDb(configName: String = "test"): Map[String, String] = {
    Map(
      ("play.slick.db.default") -> configName
    )
  }

  /**
   * Create a test application with the specified Slick config name.
   *
   * Ensure the database has been wiped first using the provided script.
   */
  def createTestApplication(configName: String = "test"): Application = {
    new GuiceApplicationBuilder().configure(withSlickDb(configName)).build()
  }

  /**
    * Empties the database ready for testing.
    *
    * @param dbConfigProvider Database configuration provider to use for the DAO(s)
    */
  def cleanDatabase(dbConfigProvider: DatabaseConfigProvider): Unit = {
    val memberDao = new MemberDAO(dbConfigProvider)

    memberDao.empty
  }
}
