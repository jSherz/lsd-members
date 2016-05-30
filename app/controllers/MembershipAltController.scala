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
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, Request, Result}
import services.MembershipService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Handles signing up new members with an e-mail address (rather than the default of a phone number).
  */
@Singleton
class MembershipAltController @Inject() (ms: MembershipService, override val messagesApi: MessagesApi)
  extends MembershipController(ms, messagesApi) {
  /**
    * The secondary sign up form (name and e-mail).
    */
  override val signupForm = Form(
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
    * Shows the alternative version of the sign-up form (name and e-mail).
    */
  override def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.signup_alt(signupForm))
  }

  /**
    * Show the alternative form with any error(s).
    *
    * @param request The user's request
    * @param errors A form, with errors
    * @return
    */
  override def showErrors(implicit request: Request[Any], errors: Form[Member]): Future[Result] =
    Future { BadRequest(views.html.signup_alt(errors)) }
}
