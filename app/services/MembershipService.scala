package services

import javax.inject._

import net.greghaines.jesque.{ConfigBuilder, Job}
import play.api.inject.ApplicationLifecycle
import models.Tables._
import net.greghaines.jesque.client.ClientImpl

/**
  * Handles the creation of members.
  */
@Singleton
class MembershipService @Inject() (appLifecycle: ApplicationLifecycle) {
  /**
    * Create a new member with the given information. Ensures that either a phone number or e-mail address was provided.
    *
    * @param member Raw member data (from the form)
    * @return Either an error message or the same member record that was provided
    */
  def signup(member: Member): Either[String, Member] = {
    if (member.valid()) {
      val job = new Job(Queues.SIGNUP_ACTION, member.getQueueJobVars())

      val client = new ClientImpl(config)
      client.enqueue(Queues.SIGNUP, job)
      client.end()

      Right(member)
    } else {
      Left("Invalid member record.")
    }
  }

  /**
    * The Jesque configuration.
    */
  private val config = new ConfigBuilder().build()
}
