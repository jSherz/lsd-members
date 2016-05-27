# text message schema

# --- !Ups

CREATE TABLE text_messages (
  id SERIAL PRIMARY KEY,
  member_id INT REFERENCES members(id) NOT NULL,
  to_number VARCHAR(20) NOT NULL,
  from_number VARCHAR(20) NOT NULL,
  sent_dt TIMESTAMP,
  sent_msid VARCHAR,
  status SMALLINT NOT NULL,
  message VARCHAR(480) NOT NULL
);

# --- !Downs

DROP TABLE text_messages;
