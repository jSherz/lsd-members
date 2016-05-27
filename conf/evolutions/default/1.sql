# members schema

# --- !Ups

CREATE TABLE members (
  id SERIAL PRIMARY KEY,
  name varchar(50),
  phone_number varchar(20),
  email varchar(255)
);

# --- !Downs

DROP TABLE members;
