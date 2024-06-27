CREATE TABLE IF NOT EXISTS backing."department" (
    name        VARCHAR(20) NOT NULL,
    description VARCHAR(60) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (name)
);