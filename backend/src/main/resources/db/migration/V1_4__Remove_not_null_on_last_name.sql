
/*
  The members.last_name field was incorrectly made NOT NULLable (breaking sign-ups).
 */

ALTER TABLE members ALTER last_name DROP NOT NULL;
