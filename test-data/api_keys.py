#!/usr/bin/env python3

import uuid
import random
import datetime
from faker import Factory
from postgres import Postgres
fake = Factory.create()
db = Postgres('postgres://luskydive@localhost/luskydive')

num_api_keys = 100

created_start = datetime.datetime(2008, 9, 1)
created_end = datetime.datetime(2017, 9, 1)


for i in range(0, num_api_keys):
  api_key_uuid = str(uuid.uuid4())

  delta = datetime.timedelta(hours = random.random() * 24 + 18)
  created_at = fake.date_time_between_dates(datetime_start = created_start, datetime_end = created_end)
  expires_at = fake.date_time_between_dates(datetime_start = created_at, datetime_end = created_at + delta)

  potential_committee_members = db.all('SELECT uuid FROM committee_members;')

  if len(potential_committee_members) == 0:
    raise ValueError('Cannot find any committee members.')
  else:
    committee_member_uuid = random.sample(potential_committee_members, 1)[0]

    print('INSERT INTO api_keys (uuid, committee_member_uuid, created_at, expires_at) VALUES(\'%s\', \'%s\', \'%s\', \'%s\');' % (api_key_uuid, committee_member_uuid, created_at.strftime('%Y-%m-%d %H:%M:%S'), expires_at.strftime('%Y-%m-%d %H:%M:%S')))
