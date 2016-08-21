
CREATE TABLE api_keys (
    uuid UUID PRIMARY KEY,
    committee_member_uuid UUID REFERENCES committee_members(uuid) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
