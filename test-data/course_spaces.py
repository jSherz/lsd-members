#!/usr/bin/env python3

import uuid
import random
import datetime
from faker import Factory
from postgres import Postgres
fake = Factory.create()
db = Postgres('postgres://luskydive@localhost/luskydive')

print('COPY course_spaces (uuid, course_uuid, number, member_uuid) FROM stdin;')

courses = db.all('SELECT uuid FROM courses ORDER BY date;')

for course in courses:
  num_spaces = 4 + random.randrange(2, 5, 1)

  for i in range(0, num_spaces + 1):
    space_uuid = str(uuid.uuid4())

    member_uuid = "\\N"

    print("%s\t%s\t%d\t%s" % (space_uuid, course, i + 1, member_uuid))

print('\\.')

