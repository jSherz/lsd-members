DELETE FROM members WHERE phone_number LIKE '070001%';
DELETE FROM members WHERE email LIKE 'e2e-auto-%';
DELETE FROM members WHERE email LIKE 'miwnvzsxjg_1500406593@tfbnw.net';

INSERT INTO api_keys (
  uuid,
  committee_member_uuid,
  created_at,
  expires_at
) VALUES (
  '%%KEY%%',
  'f5e0e6f1-1786-4702-b2b0-800774e5021b',
  now(),
  now() + INTERVAL '1 hour'
);
