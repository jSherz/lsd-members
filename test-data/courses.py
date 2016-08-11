#!/usr/bin/env python3

import uuid
import random
import datetime
from faker import Factory
from postgres import Postgres
fake = Factory.create()
db = Postgres('postgres://luskydive@localhost/luskydive')

num_years = 6
num_courses = 12

academic_year = 2009 # 2009/10

all_booked_date = datetime.datetime(2016, 8, 1)

print('COPY courses (uuid, date, organiser_uuid, secondary_organiser_uuid, status) FROM stdin;')

for year in range(academic_year, academic_year + num_years + 1):
  committee_search_start_date = datetime.datetime(year, 7, 1)
  committee_search_end_date = datetime.datetime(year + 1, 6, 1)

  course_start = datetime.datetime(year, 9, 1)
  course_end = datetime.datetime(year + 1, 7, 1)

  for i in range(0, num_courses):
    course_uuid = str(uuid.uuid4())
    date = fake.date_time_between_dates(datetime_start = course_start, datetime_end = course_end)

    potential_organisers = db.all('SELECT uuid FROM committee_members WHERE created_at < %(date)s AND created_at < %(end)s', {'date': date, 'end': committee_search_end_date})

    if len(potential_organisers) == 0:
      raise ValueError('Cannot find any committee members from ' + str(date) + ' to ' + str(committee_search_end_date))
    else:
      organiser_uuid = random.sample(potential_organisers, 1)[0]
      potential_organisers.remove(organiser_uuid)

      secondary_organiser_uuid = "\\N"

      if random.random() > 0.4:
        secondary_organiser_uuid = random.sample(potential_organisers, 1)[0]

      status = 0 if date > all_booked_date else 1

      print("%s\t%s\t%s\t%s\t%d" % (course_uuid, date.strftime('%Y-%m-%d'), organiser_uuid, secondary_organiser_uuid, status))

print('\\.')

