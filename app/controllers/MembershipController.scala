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

import javax.inject._

import models.{Member, Validators}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.twirl.api.{BaseScalaTemplate, Format, Html}
import services.MembershipService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Handles signing up new members.
 */
@Singleton
class MembershipController @Inject() (ms: MembershipService, val messagesApi: MessagesApi) extends Controller with I18nSupport {
  /**
    * The primary sign up form (name and phone number).
    */
  protected val signupForm = Form(
    mapping(
      "name" -> nonEmptyText.verifying(Validators.nameValidator),
      "phoneNumber" -> text.verifying(Validators.phoneNumberValidator)
    )((name: String, phoneNumber: String) => {
      Member(None, name, Some(phoneNumber), None)
    })((member: Member) => {
      Some(member.name, member.phoneNumber.getOrElse(""))
    })
  )

  /**
    * Shows the main version of the sign-up form (name and phone number).
    */
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.index(signupForm))
  }

  /**
    * Show the relevant form with any error(s).
    *
    * @param request The user's request
    * @param errors A form, with errors
    * @return
    */
  protected def showErrors(implicit request: Request[Any], errors: Form[Member]): Future[Result] =
    Future { BadRequest(views.html.index(errors)) }

  /**
   * Show either the membership form with errors or do the sign-up.
   */
  def signup: Action[AnyContent] = Action.async { implicit request =>
   signupForm.bindFromRequest.fold(showErrors(request, _), (member) => {
     for {
       result <- ms.signup(member)
     } yield result match {
       case Left(error) => BadRequest(views.html.membership_error(error))
       case Right(member) => Redirect(routes.MembershipController.thankYou)
     }
   })
  }

  /**
    * Display a thank you message post sign-up.
    */
  def thankYou: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.membership_thank_you())
  }
}
