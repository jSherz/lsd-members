# settings schema

# --- !Ups

CREATE TABLE settings (
    welcome_text_message varchar(480)
);

# --- !Downs

DROP TABLE settings;
