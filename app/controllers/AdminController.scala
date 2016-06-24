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

package controllers

import dao.{MemberDAO, SettingsDAO, TextMessageDAO}
import models.{Member, Setting, Settings, Validators}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.concurrent.Future
import scalaz.OptionT
import scalaz.OptionT._

/**
 * Handles the administration interface.
 */
class AdminController @javax.inject.Inject() (val messagesApi: MessagesApi, val settingsDao: SettingsDAO, val memberDao: MemberDAO,
                                 val textMessageDao: TextMessageDAO) extends Controller with I18nSupport {
  /**
    * The default welcome message (translated).
    */
  private val defaultWelcomeMessage = messagesApi.apply("settings.welcomeMessageDefault")

  /**
    * A form used to update global application settings stored in the database.
    */
  private val settingsForm = Form(
    mapping(
      "welcomeText" -> text.verifying(Validators.welcomeTextValidator)
    )(identity)(text => Some(text))
  )

  /**
    * Shows the main admin dashboard.
    */
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.admin.index())
  }

  /**
    * Shows the settings edit form, with the saved (or default) values stored for each setting.
    */
  def settings: Action[AnyContent] = Action.async { implicit request =>
    settingsDao.getOrElse(Settings.WelcomeText, defaultWelcomeMessage).map { text =>
      Ok(views.html.admin.settings(settingsForm.fill(text)))
    }
  }

  /**
    * Update the saved settings.
    */
  def updateSettings: Action[AnyContent] = Action { implicit request =>
    settingsForm.bindFromRequest.fold(formWithErrors => {
      BadRequest(views.html.admin.settings(formWithErrors))
    }, (welcomeText: String) => {
      settingsDao.put(Setting(Settings.WelcomeText, welcomeText))

      Redirect(routes.AdminController.index())
    })
  }

  /**
    * Show the saved information for a member.
    *
    * @param id Member to show details for
    * @return
    */
  def member(id: Int): Action[AnyContent] = Action.async { implicit request =>
    memberDao.get(id).flatMap {
      _ match {
        case Some(member) => textMessageDao.forMember(member).map(messages =>
          Ok(views.html.admin.member_view(member, messages))
        )
        case None => Future(NotFound(messagesApi.apply("error.memberNotFound")))
      }
    }
  }
}
