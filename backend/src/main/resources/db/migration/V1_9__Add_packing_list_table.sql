CREATE TABLE packing_list_items (
  uuid         UUID PRIMARY KEY REFERENCES members (uuid),
  warm_clothes BOOLEAN NOT NULL DEFAULT FALSE,
  sleeping_bag BOOLEAN NOT NULL DEFAULT FALSE,
  sturdy_shoes BOOLEAN NOT NULL DEFAULT FALSE,
  cash         BOOLEAN NOT NULL DEFAULT FALSE,
  toiletries   BOOLEAN NOT NULL DEFAULT FALSE
);
