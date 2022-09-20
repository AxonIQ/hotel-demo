
-- this script is compatible with PostgreSQL only
-- to use in other databases please adapt the script

DROP TABLE IF EXISTS association_value_entry;
DROP TABLE IF EXISTS token_entry;
DROP TABLE IF EXISTS saga_entry;

CREATE TABLE IF NOT EXISTS association_value_entry
(
    id SERIAL,
    association_key VARCHAR(255) NOT NULL,
    association_value VARCHAR(255),
    saga_id VARCHAR(255) NOT NULL,
    saga_type VARCHAR(255),
    PRIMARY KEY (id)
    );

CREATE INDEX IF NOT EXISTS association_value_entry_saga_type_association_key_association_value_idx
    on association_value_entry (saga_type, association_key, association_value);

CREATE INDEX IF NOT EXISTS association_value_entry_saga_id_saga_type_idx
    on association_value_entry (saga_id, saga_type);

CREATE TABLE IF NOT EXISTS token_entry
(
    processor_name VARCHAR(255) NOT NULL,
    segment INTEGER NOT NULL,
    owner VARCHAR(255),
    timestamp VARCHAR(255) NOT NULL,
    token bytea,
    token_type VARCHAR(255),
    PRIMARY KEY (processor_name, segment)
    );

CREATE TABLE IF NOT EXISTS saga_entry
(
    saga_id VARCHAR(255) NOT NULL,
    revision VARCHAR(255),
    saga_type VARCHAR(255),
    serialized_saga bytea,
    PRIMARY KEY (saga_id)
    );

