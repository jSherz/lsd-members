#!/usr/bin/env python3

import uuid
from passlib.hash import pbkdf2_sha512

password = input('Enter password: ')

password_parts = pbkdf2_sha512.encrypt(password, salt_size=32).split('$')

password = password_parts[4]
salt = password_parts[3]

def convert_b64(input):
  return input.replace('.', '+') + '='

print('Password: ' + convert_b64(password))
print('Salt: ' + convert_b64(salt))

