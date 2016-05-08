package services

import javax.inject._

import net.greghaines.jesque.{ConfigBuilder, Job}
import play.api.inject.ApplicationLifecycle
import models.CaseClassMappings._
import net.greghaines.jesque.client.ClientImpl
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import play.api.libs.concurrent.Execution.Implicits._

/**
  * Handles the creation of members.
  */
@Singleton
class MembershipService @Inject() (appLifecycle: ApplicationLifecycle, dbConfigProvider: DatabaseConfigProvider) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  /**
    * Create a new member with the given information. Ensures that either a phone number or e-mail address was provided.
    *
    * @param member Raw member data (from the form)
    * @return Either an error message or the same member record that was provided
    */
  def signup(member: Member): Future[Either[String, Member]] = {
    if (member.valid()) {
      // Attempt to find a member by the phone number or e-mail given
      val memberByPhoneNumber = members.filter(_.phoneNumber === member.phoneNumber).length

      dbConfig.db.run(memberByPhoneNumber.result).map { r =>
        if (r >= 1) {
          Left("Member exists with that phone number.")
        } else {
          val job = new Job(Queues.SIGNUP_ACTION, member.getQueueJobVars())

          val client = new ClientImpl(config)
          client.enqueue(Queues.SIGNUP, job)
          client.end()

          Right(member)
        }
      }

      //val memberByEmail = members.filter(_.email === member.email).map(_.id).length
//
      //Await.result(dbConfig.db.run(
      //  if (memberByPhoneNumber.result >= 1) {
      //    Left("Member exists with that phone number.")
      //  } else if (memberByEmail.result >= 1) {
      //    Left("Member exists with that e-mail address.")
      //  } else {
      //    val job = new Job(Queues.SIGNUP_ACTION, member.getQueueJobVars())
//
      //    val client = new ClientImpl(config)
      //    client.enqueue(Queues.SIGNUP, job)
      //    client.end()
//
      //    Right(member)
      //  }
      //  memberByEmail.result
      //), Duration.Inf)
    } else {
      Future(Left("Invalid member record."))
    }
  }

  /**
    * The Jesque configuration.
    */
  private val config = new ConfigBuilder().build()
}
