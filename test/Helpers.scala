import play.api.db.slick.DatabaseConfigProvider

import scala.io.Source
import slick.driver.PostgresDriver.api._
import play.api.{Application, Play}
import play.api.inject.guice.GuiceApplicationBuilder
import slick.driver.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global

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
    val app = new GuiceApplicationBuilder().configure(withSlickDb(configName)).build()

    val dbConfig = DatabaseConfigProvider.get[JdbcProfile](app)

    val cleanScriptLookup = app.getExistingFile("conf/clean-db.sql")

    cleanScriptLookup match {
      case Some(cleanScriptPath) => {
        val cleanScriptFile = Source.fromFile(cleanScriptPath)
        val cleanScript = try cleanScriptFile.mkString finally cleanScriptFile.close()

        println(cleanScript)

        dbConfig.db.run(DBIO.seq(sqlu"TRUNCATE TABLE members;"))
      }
      case None => throw new IllegalArgumentException("No database cleaning script found at 'conf/clean-db.sql'!")
    }

    app
  }
}
