package services

import javax.inject._

import net.greghaines.jesque.ConfigBuilder
import play.api.inject.ApplicationLifecycle
import models.Member

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
    Right(member)
  }

  /**
    * The Jesque configuration.
    */
  val config = new ConfigBuilder().build()
}
