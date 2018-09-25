/**
  * MIT License
  *
  * Copyright (c) 2016-2018 James Sherwood-Jones <james.sherwoodjones@gmail.com>
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

package com.jsherz.luskydive.dao

import java.sql.{Date, Timestamp}
import java.util.UUID

import com.jsherz.luskydive.core._
import com.jsherz.luskydive.services.DatabaseService
import slick.lifted.ForeignKeyQuery

/**
  * All of the database tables, modelled as Slick objects.
  */
class Tables(protected val databaseService: DatabaseService) {

  protected val driver = databaseService.driver
  protected val db = databaseService.db

  import driver.api._

  /**
    * A field length that can store mobile numbers in any of the allowed formats (see Validators).
    */
  private val MOBILE_NUMBER_FIELD_LENGTH: Int = 20

  /**
    * The most text that can be sent in a welcome text message (typically 3 standard text messages or 480).
    */
  private val TEXT_MESSAGE_LENGTH: Int = 480

  /**
    * Max length of a member's name.
    */
  private val NAME_FIELD_LENGTH: Int = 50

  /**
    * Max length of a member's e-mail.
    */
  private val EMAIL_FIELD_LENGTH: Int = 255

  protected val Settings: TableQuery[SettingsTable] = TableQuery[SettingsTable]

  /**
    * The Slick mapping for the settings table.
    *
    * @param tag
    */
  protected class SettingsTable(tag: Tag) extends Table[Setting](tag, "settings") {

    def key: Rep[String] = column[String]("key", O.PrimaryKey)

    def value: Rep[String] = column[String]("value")

    def * = (key, value) <> (Setting.tupled, Setting.unapply)

  }

  protected val Members: TableQuery[MembersTable] = TableQuery[MembersTable]

  /**
    * The Slick mapping for the members table.
    *
    * @param tag
    */
  protected class MembersTable(tag: Tag) extends Table[Member](tag, "members") {

    def uuid: Rep[UUID] = column[UUID]("uuid", O.PrimaryKey)

    def firstName: Rep[String] = column[String]("first_name", O.Length(NAME_FIELD_LENGTH, varying = true))

    def lastName: Rep[Option[String]] = column[Option[String]]("last_name", O.Length(NAME_FIELD_LENGTH, varying = true))

    def phoneNumber: Rep[Option[String]] = column[Option[String]]("phone_number")

    def email: Rep[Option[String]] = column[Option[String]]("email", O.Length(EMAIL_FIELD_LENGTH, varying = true))

    def lastJump: Rep[Option[Date]] = column[Option[Date]]("last_jump")

    def weight: Rep[Option[Short]] = column[Option[Short]]("weight")

    def height: Rep[Option[Short]] = column[Option[Short]]("height")

    def driver: Rep[Boolean] = column[Boolean]("driver")

    def organiser: Rep[Boolean] = column[Boolean]("organiser")

    def createdAt: Rep[Timestamp] = column[Timestamp]("created_at")

    def updatedAt: Rep[Timestamp] = column[Timestamp]("updated_at")

    def socialUserId: Rep[Option[String]] = column[Option[String]]("social_user_id")

    def * = (uuid, firstName, lastName, phoneNumber, email, lastJump,
      weight, height, driver, organiser, createdAt, updatedAt, socialUserId) <> (Member.tupled, Member.unapply)

  }

  protected val MassTexts: TableQuery[MassTextsTable] = TableQuery[MassTextsTable]

  /**
    * The Slick mapping for mass texts being sent to members.
    *
    * @param tag
    */
  protected class MassTextsTable(tag: Tag) extends Table[MassText](tag, "mass_texts") {

    def uuid: Rep[UUID] = column[UUID]("uuid", O.PrimaryKey)

    def committeeMemberUuid: Rep[UUID] = column[UUID]("committee_member_uuid")

    def template: Rep[String] = column[String]("template")

    def createdAt: Rep[Timestamp] = column[Timestamp]("created_at")

    def * = (uuid, committeeMemberUuid, template, createdAt) <> (MassText.tupled, MassText.unapply)

  }

  protected val TextMessages: TableQuery[TextMessagesTable] = TableQuery[TextMessagesTable]

  /**
    * The Slick mapping for text messages being sent to members.
    *
    * @param tag
    */
  protected class TextMessagesTable(tag: Tag) extends Table[TextMessage](tag, "text_messages") {

    def uuid: Rep[UUID] = column[UUID]("uuid", O.PrimaryKey)

    def memberUuid: Rep[UUID] = column[UUID]("member_uuid")

    def massTextUuid: Rep[Option[UUID]] = column[Option[UUID]]("mass_text_uuid")

    def status: Rep[Short] = column[Short]("status")

    def toNumber: Rep[String] = column[String]("to_number", O.Length(MOBILE_NUMBER_FIELD_LENGTH, varying = true))

    def fromNumber: Rep[String] = column[String]("from_number", O.Length(MOBILE_NUMBER_FIELD_LENGTH, varying = true))

    def message: Rep[String] = column[String]("message", O.Length(TEXT_MESSAGE_LENGTH, varying = true))

    def externalId: Rep[Option[String]] = column[Option[String]]("external_id")

    def fromMember: Rep[Boolean] = column[Boolean]("from_member")

    def createdAt: Rep[Timestamp] = column[Timestamp]("created_at")

    def updatedAt: Rep[Timestamp] = column[Timestamp]("updated_at")

    def * = (uuid, memberUuid, massTextUuid, status, toNumber, fromNumber, message, externalId, fromMember, createdAt,
      updatedAt) <> (TextMessage.tupled, TextMessage.unapply)

  }

  protected val TextMessageErrors: TableQuery[TextMessageErrorsTable] = TableQuery[TextMessageErrorsTable]

  /**
    * The Slick mapping for the text_message_errors object.
    *
    * @param tag
    */
  protected class TextMessageErrorsTable(tag: Tag) extends Table[TextMessageError](tag, "text_message_errors") {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def textMessageId: Rep[Int] = column[Int]("text_message_id")

    def timestamp: Rep[Timestamp] = column[Timestamp]("timestamp")

    def message: Rep[String] = column[String]("message")

    def * = (id.?, textMessageId, timestamp, message) <> (TextMessageError.tupled, TextMessageError.unapply)

  }

  protected val CommitteeMembers: TableQuery[CommitteeMemberTables] = TableQuery[CommitteeMemberTables]

  /**
    * The Slick mapping for committee members.
    *
    * @param tag
    */
  protected class CommitteeMemberTables(tag: Tag) extends Table[CommitteeMember](tag, "committee_members") {

    def uuid: Rep[UUID] = column[UUID]("uuid", O.PrimaryKey)

    def memberUuid: Rep[UUID] = column[UUID]("member_uuid")

    def name: Rep[String] = column[String]("name")

    def email: Rep[String] = column[String]("email")

    def password: Rep[String] = column[String]("password")

    def salt: Rep[String] = column[String]("salt")

    def locked: Rep[Boolean] = column[Boolean]("locked")

    def createdAt: Rep[Timestamp] = column[Timestamp]("created_at")

    def updatedAt: Rep[Timestamp] = column[Timestamp]("updated_at")

    def * = (uuid, memberUuid, name, email, password, salt, locked, createdAt, updatedAt) <> (CommitteeMember.tupled, CommitteeMember.unapply)

  }

  protected val Courses: TableQuery[CoursesTable] = TableQuery[CoursesTable]

  /**
    * The Slick mapping for (typically static line) courses.
    *
    * @param tag
    */
  protected class CoursesTable(tag: Tag) extends Table[Course](tag, "courses") {

    def uuid: Rep[UUID] = column[UUID]("uuid", O.PrimaryKey)

    def date: Rep[Date] = column[Date]("date")

    def organiserUuid: Rep[UUID] = column[UUID]("organiser_uuid")

    def secondaryOrganiserUuid: Rep[Option[UUID]] = column[Option[UUID]]("secondary_organiser_uuid")

    def status: Rep[Int] = column[Int]("status")

    def * = (uuid, date, organiserUuid, secondaryOrganiserUuid, status) <> (Course.tupled, Course.unapply)

  }

  protected val CourseSpaces: TableQuery[CourseSpacesTable] = TableQuery[CourseSpacesTable]

  /**
    * The Slick mapping for a space on a course.
    *
    * @param tag
    */
  protected class CourseSpacesTable(tag: Tag) extends Table[CourseSpace](tag, "course_spaces") {

    def uuid: Rep[UUID] = column[UUID]("uuid", O.PrimaryKey)

    def courseUuid: Rep[UUID] = column[UUID]("course_uuid")

    def number: Rep[Int] = column[Int]("number")

    def memberUuid: Rep[Option[UUID]] = column[Option[UUID]]("member_uuid")

    def depositPaid: Rep[Boolean] = column[Boolean]("deposit_paid")

    def course: ForeignKeyQuery[CoursesTable, Course] =
      foreignKey("course_spaces_course_uuid_number_key", courseUuid, Courses)(_.uuid, ForeignKeyAction.Cascade)

    def * = (uuid, courseUuid, number, memberUuid, depositPaid) <> (CourseSpace.tupled, CourseSpace.unapply)

  }

  protected val PackingListItems: TableQuery[PackingListTable] = TableQuery[PackingListTable]

  protected class PackingListTable(tag: Tag) extends Table[PackingListItem](tag, "packing_list_items") {

    def uuid: Rep[UUID] = column[UUID]("uuid", O.PrimaryKey)

    def warmClothes: Rep[Boolean] = column[Boolean]("warm_clothes")

    def sleepingBag: Rep[Boolean] = column[Boolean]("sleeping_bag")

    def sturdyShoes: Rep[Boolean] = column[Boolean]("sturdy_shoes")

    def cash: Rep[Boolean] = column[Boolean]("cash")

    def toiletries: Rep[Boolean] = column[Boolean]("toiletries")

    def member: ForeignKeyQuery[MembersTable, Member] =
      foreignKey("packing_list_items_uuid_fkey", uuid, Members)(_.uuid, ForeignKeyAction.Cascade)

    def * = (uuid, warmClothes, sleepingBag, sturdyShoes, cash, toiletries) <> (PackingListItem.tupled, PackingListItem.unapply)

  }

}
