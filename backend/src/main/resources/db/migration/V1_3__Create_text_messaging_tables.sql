
CREATE TABLE mass_texts (
  uuid UUID PRIMARY KEY,
  committee_member_uuid UUID REFERENCES committee_members(uuid) NOT NULL,
  template VARCHAR(480) NOT NULL,

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE text_messages (
  uuid UUID PRIMARY KEY,
  member_uuid UUID REFERENCES members(uuid) NOT NULL,
  mass_text_uuid UUID REFERENCES mass_texts(uuid),
  status SMALLINT NOT NULL,
  to_number VARCHAR(20) NOT NULL,
  from_number VARCHAR(20) NOT NULL,
  message VARCHAR(480) NOT NULL,
  external_id VARCHAR(100),

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
