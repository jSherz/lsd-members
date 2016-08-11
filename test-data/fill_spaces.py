#!/usr/bin/env python3

import uuid
import random
import datetime
from faker import Factory
from postgres import Postgres
fake = Factory.create()
db = Postgres('postgres://luskydive@localhost/luskydive')

update_query = 'UPDATE course_spaces SET member_uuid = \'%s\' WHERE uuid = \'%s\';'

spaces = db.all('SELECT course_spaces.uuid, courses.date FROM courses INNER JOIN course_spaces ON courses.uuid = course_spaces.course_uuid ORDER BY date;')

for space in spaces:
  if (random.random() > 0.4):
    potential_members = db.all('SELECT uuid FROM members WHERE created_at < %(date)s AND date_part(\'month\', age(%(date)s, created_at)) <= 9', {'date': space.date})

    member_uuid = random.sample(potential_members, 1)[0]

    print(update_query % (member_uuid, str(space.uuid)))

