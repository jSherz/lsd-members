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
  private val signupForm = Form(
    mapping(
      "name" -> nonEmptyText.verifying(Validators.nameValidator),
      "phoneNumber" -> text.verifying(Validators.phoneNumberValidator)
    )((name: String, phoneNumber: String) => {
      Member(None, name, Some(phoneNumber), None)
    })((member: Member) => {
      Some(member.name, member.phoneNumber.get)
    })
  )

  /**
    * The secondary sign up form (name and e-mail).
    */
  private val signupAltForm = Form(
    mapping(
      "name" -> nonEmptyText.verifying(Validators.nameValidator),
      "email" -> text.verifying(Validators.emailValidator)
    )((name: String, email: String) => {
      Member(None, name, None, Some(email))
    })((member: Member) => {
      Some(member.name, member.email.get)
    })
  )

  /**
    * Shows the main version of the sign-up form (name and phone number).
    */
  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.index(signupForm, false))
  }

  /**
    * Shows the alternative version of the sign-up form (name and e-mail address).
    */
  def alt: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.index(signupAltForm, true))
  }

  /**
    * Create a member - called after validating the form.
    *
    * @param member The member object with either a phone number or e-mail address
    * @return Result of attempting to create the member
    */
  private def createMember(implicit request: Request[Any], member: Member): Future[Result] = {
    val result = ms.signup(member)

    result.map { result =>
      result match {
        case Left(error) => BadRequest(views.html.membership_error(error))
        case Right(member) => Redirect(routes.MembershipController.thankYou)
      }
    }
  }

  /**
    * Show the relevant main form with any error(s).
    *
    * @param request The user's request
    * @param errors A form, with errors
    * @param altForm If the user was brought from the alternative form
    * @return
    */
  private def showErrors(implicit request: Request[Any], errors: Form[Member], altForm: Boolean): Future[Result] = {
    Future[Result] {
      BadRequest(views.html.index(errors, altForm))
    }
  }

  /**
   * Membership signup with a mobile phone number.
   */
  def signup: Action[AnyContent] = Action.async { implicit request =>
   signupForm.bindFromRequest.fold(showErrors(request, _, false), createMember(request, _))
  }

  /**
    * Alternative signup form - with e-mail address.
    */
  def signupAlt: Action[AnyContent] = Action.async { implicit request =>
    signupAltForm.bindFromRequest.fold(showErrors(request, _, true), createMember(request, _))
  }

  /**
    * Display a thank you message.
    *
    * @return
    */
  def thankYou: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.membership_thank_you())
  }
}
