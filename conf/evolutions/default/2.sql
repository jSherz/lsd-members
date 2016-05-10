# settings schema

# --- !Ups

CREATE TABLE settings (
    key VARCHAR(50),
    value VARCHAR(500)
);

# --- !Downs

DROP TABLE settings;
