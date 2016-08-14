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

package com.jsherz.luskydive.fixtures

import java.util.UUID

import com.jsherz.luskydive.core.{CourseSpace, CourseWithOrganisers}
import com.jsherz.luskydive.json.{CourseOrganiser, CourseSpaceWithMember, StrippedMember}

/**
  * Examples of [[CourseWithOrganisers]].
  */
object CoursesWithOrganisers {

  val courseWithOrganisersA = CourseWithOrganisers(
    Courses.courseA,
    CourseOrganiser(CommitteeMembers.committeeMemberA.uuid.get, CommitteeMembers.committeeMemberA.name),
    None
  )

  val courseWithOrganisersASpaces = Seq(
    CourseSpaceWithMember(Some(UUID.fromString("f543c4c2-7e07-4e2a-8ce3-f2e653008daa")), Courses.courseA.uuid.get, 1,
      Some(StrippedMember(Some(UUID.fromString("61e6c06f-295c-49e9-bf2b-b3a8dda299e2")), "Callum Scott"))),
    CourseSpaceWithMember(Some(UUID.fromString("16daf695-5509-4cb7-ae40-6e04c7ce4b44")), Courses.courseA.uuid.get, 2, None),
    CourseSpaceWithMember(Some(UUID.fromString("ff526943-55f2-4b51-a863-e3ce800eabf7")), Courses.courseA.uuid.get, 3, None),
    CourseSpaceWithMember(Some(UUID.fromString("2a14e024-d165-4147-9d98-00463bbf93c9")), Courses.courseA.uuid.get, 4,
      Some(StrippedMember(Some(UUID.fromString("8556d0dd-05e9-44e6-8b66-d9607240387e")), "Brandon Gardner"))),
    CourseSpaceWithMember(Some(UUID.fromString("38c429c2-8147-4c56-a9bb-8675647fbf51")), Courses.courseA.uuid.get, 5,
      Some(StrippedMember(Some(UUID.fromString("98d9c289-f966-4767-8f9d-ec0a17831a27")), "Alicia Moore"))),
    CourseSpaceWithMember(Some(UUID.fromString("c914214c-4498-4506-b9dc-fc1f7d715215")), Courses.courseA.uuid.get, 6,
      Some(StrippedMember(Some(UUID.fromString("35c816a9-eb6f-4fe2-bea2-15a340a81508")), "Samuel Barber"))),
    CourseSpaceWithMember(Some(UUID.fromString("bae4cba2-e6d1-4dd3-ad6c-a6a452aad646")), Courses.courseA.uuid.get, 7, None),
    CourseSpaceWithMember(Some(UUID.fromString("c8bfcaca-e55e-4f32-9f3f-782b72d8acfa")), Courses.courseA.uuid.get, 8, None)
  )

}
