# members schema

# --- !Ups

CREATE TABLE members (
    id SERIAL PRIMARY KEY,
    name varchar(50),
    phone_number varchar(16),
    email varchar(255)
);

# --- !Downs

DROP TABLE members;
