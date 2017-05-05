/*
  Present if the user has connected their account to their FB.
 */

ALTER TABLE members
  ADD COLUMN social_user_id VARCHAR(20) NULL;
