# text message schema

# --- !Ups

CREATE TABLE text_messages (
    id SERIAL PRIMARY KEY,
    member_id INT REFERENCES members(id) NOT NULL,
    to_number VARCHAR(15) NOT NULL,
    from_number VARCHAR(15) NOT NULL,
    sent_dt TIMESTAMP NOT NULL,
    status SMALLINT NOT NULL,
    message VARCHAR(480) NOT NULL
);

# --- !Downs

DROP TABLE text_messages;
