CREATE TABLE person (
    id BIGSERIAL PRIMARY KEY,
    household_id BIGINT NOT NULL REFERENCES household (id),
    member_id VARCHAR(64) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    licensing_worker VARCHAR(255),
    license_number VARCHAR(255),
    license_status VARCHAR(255),
    permission VARCHAR(255),
    internal_notes TEXT,
    flagged BOOLEAN NOT NULL DEFAULT FALSE,
    flag_reason VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT person_member_id_unique UNIQUE (member_id)
)