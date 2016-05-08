package services

import javax.inject._

import dao.MemberDAO
import models.Member
import net.greghaines.jesque.{ConfigBuilder, Job}
import net.greghaines.jesque.client.ClientImpl
import slick.driver.JdbcProfile

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._

/**
  * Handles the creation of members.
  */
@Singleton
class MembershipService @Inject() (memberDao: MemberDAO) {
  /**
    * Create a new member with the given information. Ensures that either a phone number or e-mail address was provided.
    *
    * @param member Raw member data (from the form)
    * @return Either an error message or the same member record that was provided
    */
  def signup(member: Member): Future[Either[String, Member]] = {
    if (member.valid()) {
      // Attempt to find a member by the phone number or e-mail given
      val memberExists = memberDao.exists(member.phoneNumber, member.email)

      memberExists.map { exists =>
        if (exists) {
          Left("error.memberExists")
        } else {
          memberDao.insert(member)

          val job = new Job(Queues.SIGNUP_ACTION, member.getQueueJobVars())

          val client = new ClientImpl(config)
          client.enqueue(Queues.SIGNUP, job)
          client.end()

          Right(member)
        }
      }
    } else {
      Future(Left("Invalid member record."))
    }
  }

  /**
    * The Jesque configuration.
    */
  private val config = new ConfigBuilder().build()
}
