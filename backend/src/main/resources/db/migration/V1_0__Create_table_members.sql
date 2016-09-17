
/* Create members table */

CREATE TABLE members (
    uuid UUID PRIMARY KEY,
    first_name VARCHAR(25) NOT NULL,
    last_name VARCHAR(25) NOT NULL,
    email VARCHAR(255) UNIQUE NULL,
    phone_number VARCHAR(20) UNIQUE NULL,
    last_jump DATE NULL,
    weight SMALLINT NULL,
    height SMALLINT NULL,
    driver BOOLEAN DEFAULT FALSE NOT NULL,
    organiser BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
)
