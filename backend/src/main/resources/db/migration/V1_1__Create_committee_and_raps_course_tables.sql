
CREATE TABLE committee_members (
    uuid UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(100) NOT NULL,
    salt VARCHAR(100) NOT NULL,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE courses (
    uuid UUID PRIMARY KEY,
    date DATE NOT NULL,
    organiser_uuid UUID REFERENCES committee_members(uuid) NOT NULL,
    secondary_organiser_uuid UUID REFERENCES committee_members(uuid),
    status INT
);

CREATE TABLE course_spaces (
    uuid UUID PRIMARY KEY,
    course_uuid UUID REFERENCES courses(uuid) NOT NULL,
    number INT,
    member_uuid UUID REFERENCES members(uuid),
    UNIQUE (uuid, number)
);
