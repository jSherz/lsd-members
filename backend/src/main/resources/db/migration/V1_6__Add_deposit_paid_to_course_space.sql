/*
  Has the member on this course space paid their deposit?
 */

ALTER TABLE course_spaces
  ADD COLUMN deposit_paid BOOLEAN DEFAULT FALSE NOT NULL;
