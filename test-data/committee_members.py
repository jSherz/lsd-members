#!/usr/bin/env python3

import uuid
import random
import datetime
from passlib.hash import pbkdf2_sha512
from faker import Factory
fake = Factory.create()

num_people = 90

created_start = datetime.datetime(2008, 9, 1)
created_end = datetime.datetime(2017, 9, 1)

this_year = datetime.datetime(2016, 7, 1)

print('COPY committee_members (uuid, name, email, password, salt, locked, created_at, updated_at) FROM stdin;')

for i in range(0, num_people):
  committee_uuid = str(uuid.uuid4())
  name = fake.name()
  email = fake.email()

  created_at = fake.date_time_between_dates(datetime_start = created_start, datetime_end = created_end)
  updated_at = fake.date_time_between_dates(datetime_start = created_at, datetime_end = created_end).strftime('%Y-%m-%d %H:%M:%S')

  locked = 'TRUE'

  if created_at > this_year:
    locked = 'FALSE'

  password_parts = pbkdf2_sha512.encrypt(email + '_Hunter2', salt_size=32).split('$')
  password = password_parts[4]
  salt = password_parts[3]

  print("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s" % (committee_uuid, name, email, password, salt, locked, created_at.strftime('%Y-%m-%d %H:%M:%S'), updated_at))

print('\\.')

