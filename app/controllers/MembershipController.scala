package controllers

import javax.inject._
import play.api._
import play.api.mvc._

/**
 * Handles memberships.
 */
@Singleton
class MembershipController @Inject() extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def signup = Action {
    Ok(views.html.membership_result("Your new application is ready."))
  }

}
