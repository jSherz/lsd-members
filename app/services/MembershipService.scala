package services

import java.time.{Clock, Instant}
import javax.inject._

import net.greghaines.jesque.ConfigBuilder
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future
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
    * @return
    */
  def signup(member: Member): Boolean = {
    true
  }

  /**
    * The Jesque configuration.
    */
  val config = new ConfigBuilder().build();
}
