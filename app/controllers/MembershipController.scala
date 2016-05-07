package controllers

import javax.inject._

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import services.MembershipService
import models.{Member, Validators}
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi

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
      "name" -> nonEmptyText,
      "phoneNumber" -> text.verifying(Validators.phoneNumberValidator)
    )((name: String, phoneNumber: String) => {
      Member(name, phoneNumber, null)
    })((member: Member) => {
      Some(member.name, member.phoneNumber)
    })
  )

  /**
    * The secondary sign up form (name and e-mail).
    */
  private val signupAltForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "email" -> text.verifying(Validators.emailValidator)
    )((name: String, email: String) => {
      Member(name, null, email)
    })((member: Member) => {
      Some(member.name, member.email)
    })
  )

  /**
    * Shows the main version of the sign-up form (name and phone number).
    */
  def index = Action { implicit request =>
    Ok(views.html.index(signupForm, false))
  }

  /**
    * Shows the alternative version of the sign-up form (name and e-mail address).
    */
  def alt = Action { implicit request =>
    Ok(views.html.index(signupAltForm, true))
  }

  /**
    * Create a member - called after validating the form.
    *
    * @param member The member object with either a phone number or e-mail address
    * @return Result of attempting to create the member
    */
  private def createMember(implicit request: play.api.mvc.Request[Any], member: Member) = {
    val result = ms.signup(member)

    result match {
      case Left(error) => BadRequest(views.html.membership_error(error))
      case Right(member) => Redirect(routes.MembershipController.thankYou)
    }
  }

  /**
   * Membership signup with a mobile phone number.
   */
  def signup = Action { implicit request =>
    signupForm.bindFromRequest.fold(errors => { BadRequest(views.html.index(errors, false)) }, createMember(request, _))
  }

  /**
    * Alternative signup form - with e-mail address.
    */
  def signupAlt = Action { implicit request =>
    signupAltForm.bindFromRequest.fold(errors => { BadRequest(views.html.index(errors, true)) }, createMember(request, _))
  }

  /**
    * Display a thank you message.
    * @return
    */
  def thankYou = Action { implicit request =>
    Ok(views.html.membership_thank_you())
  }
}
