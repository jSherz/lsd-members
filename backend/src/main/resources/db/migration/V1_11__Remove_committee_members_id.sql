ALTER TABLE courses
  DROP CONSTRAINT courses_secondary_organiser_uuid_fkey;

ALTER TABLE courses
  DROP CONSTRAINT courses_organiser_uuid_fkey;

UPDATE courses
SET organiser_uuid           = cm.member_uuid,
    secondary_organiser_uuid = cm2.member_uuid
FROM courses as c
       INNER JOIN committee_members cm ON c.organiser_uuid = cm.uuid
       LEFT OUTER JOIN committee_members cm2 on c.secondary_organiser_uuid = cm2.uuid;

ALTER TABLE mass_texts
  DROP CONSTRAINT mass_texts_committee_member_uuid_fkey;

UPDATE mass_texts
SET committee_member_uuid = cm.member_uuid
FROM mass_texts as mt
       INNER JOIN committee_members cm ON mt.committee_member_uuid = cm.uuid;

ALTER TABLE committee_members
  DROP CONSTRAINT committee_members_pkey;

ALTER TABLE committee_members
  DROP CONSTRAINT committee_members_member_uuid_key;

DROP INDEX IF EXISTS committee_members_pkey;
DROP INDEX IF EXISTS committee_members_member_uuid_key;

ALTER TABLE committee_members
  DROP COLUMN uuid;

CREATE UNIQUE INDEX committee_members_pkey
  ON committee_members (member_uuid);

ALTER TABLE committee_members
  ADD CONSTRAINT committee_members_member_uuid_key
FOREIGN KEY (member_uuid) REFERENCES members (uuid);

ALTER TABLE mass_texts
  ADD CONSTRAINT mass_texts_committee_member_uuid_fkey
FOREIGN KEY (committee_member_uuid) REFERENCES committee_members (member_uuid);

ALTER TABLE courses
  ADD CONSTRAINT courses_secondary_organiser_uuid_fkey
FOREIGN KEY (secondary_organiser_uuid) REFERENCES committee_members (member_uuid);

ALTER TABLE courses
  ADD CONSTRAINT courses_organiser_uuid_fkey
FOREIGN KEY (organiser_uuid) REFERENCES committee_members (member_uuid);
