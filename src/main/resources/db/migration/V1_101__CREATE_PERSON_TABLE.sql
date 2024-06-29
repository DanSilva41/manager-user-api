CREATE TABLE IF NOT EXISTS backing.person (
    code            INT             NOT NULL,
    first_name      VARCHAR(60)     NOT NULL,
    last_name       VARCHAR(60)     NOT NULL,
    email           VARCHAR(100)    NOT NULL,
    version         INT             NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,
    CONSTRAINT pk_person PRIMARY KEY (code),
    CONSTRAINT un_person_email UNIQUE (email)
);

CREATE SEQUENCE backing.person_code_seq
    INCREMENT BY 1
    START WITH 1;