# text message error schema

# --- !Ups

CREATE TABLE text_message_errors (
  id SERIAL PRIMARY KEY,
  text_message_id INT REFERENCES text_messages(id) NOT NULL,
  timestamp TIMESTAMP NOT NULL,
  message VARCHAR NOT NULL
);

# --- !Downs

DROP TABLE text_message_errors;
