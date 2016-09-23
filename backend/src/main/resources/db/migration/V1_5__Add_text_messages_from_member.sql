/*
  Is this text message from the member?

  FALSE -> sent by us to member
  TRUE -> sent by member to us
 */

ALTER TABLE text_messages
  ADD COLUMN from_member BOOLEAN DEFAULT FALSE NOT NULL;

COMMENT ON COLUMN text_messages.from_member IS 'Was this text message sent from the member to us?';
