package controllers

import javax.inject._

import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import services.MembershipService
import models.Member
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
      "phoneNumber" -> nonEmptyText
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
      "email" -> nonEmptyText
    )((name: String, email: String) => {
      Member(name, null, email)
    })((member: Member) => {
      Some(member.name, member.email)
    })
  )

  /**
    * Shows the main version of the sign-up form (name and phone number).
    */
  def index = Action {
    Ok(views.html.index(signupForm))
  }

  /**
    * Shows the alternative version of the sign-up form (name and e-mail address).
    */
  def alt = Action {
    Ok(views.html.alt(signupAltForm))
  }

  /**
    * Create a member - called after validating the form.
    *
    * @param member The member object with either a phone number or e-mail address
    * @return Result of attempting to create the member
    */
  private def createMember(member: Member) = {
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
    signupForm.bindFromRequest.fold(errors => { BadRequest(views.html.index(errors)) }, createMember)
  }

  /**
    * Alternative signup form - with e-mail address.
    */
  def signupAlt = Action { implicit request =>
    signupAltForm.bindFromRequest.fold(errors => { BadRequest(views.html.alt(errors)) }, createMember)
  }

  /**
    * Display a thank you message.
    * @return
    */
  def thankYou = Action {
    Ok(views.html.membership_thank_you())
  }
}
