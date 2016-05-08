package controllers

import javax.inject._

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import models.{Member, Validators}
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

/**
 * Handles the administration interface.
 */
@Singleton
class AdminController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  /**
    * Shows the main admin screen.
    */
  def index = Action { implicit request =>
    Ok(views.html.admin.index())
  }
}
