CREATE TABLE role (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    label VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT role_code_unique UNIQUE (code)
);

CREATE TABLE person_role (
    person_id BIGINT NOT NULL REFERENCES person (id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES role (id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (person_id, role_id)
)