#!/usr/bin/env python3

import uuid
import random
import datetime
from hashlib import sha256
from faker import Factory
from postgres import Postgres
fake = Factory.create()
db = Postgres('postgres://luskydive@localhost/luskydive')

print('COPY text_messages (uuid, member_uuid, mass_text_uuid, status, to_number, from_number, message, external_id, created_at, updated_at) FROM stdin;')

mass_texts = db.all('SELECT uuid, template, created_at FROM mass_texts;')

for mass_text in mass_texts:
  start = datetime.datetime(mass_text.created_at.year, 8, 1)
  end = datetime.datetime(mass_text.created_at.year + 1, 7, 28)
  phone_number = '+447' + str(random.randrange(100000000, 999999999, 1))

  members = db.all('SELECT uuid, phone_number, name FROM members WHERE phone_number IS NOT NULL AND created_at > %(start)s AND created_at < %(end)s', { 'start': start, 'end': end });

  for member in members:
    created_at = mass_text.created_at
    delta = datetime.timedelta(seconds = random.random() * 7200 + 5)
    updated_at = mass_text.created_at + delta

    message = mass_text.template.replace('{{ name }}', member.name)
    text_uuid = str(uuid.uuid4())
    external_id = sha256(text_uuid.encode('utf-8')).hexdigest()

    print("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s" % (text_uuid, member.uuid, mass_text.uuid, 1, member.phone_number, phone_number, message, external_id, created_at, updated_at))

print('\\.')

