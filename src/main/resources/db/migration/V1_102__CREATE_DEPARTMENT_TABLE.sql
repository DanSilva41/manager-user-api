CREATE TABLE IF NOT EXISTS backing."department" (
    code        INT             NOT NULL,
    name        VARCHAR(60)     NOT NULL,
    description VARCHAR(100)    NOT NULL,
    created_at  TIMESTAMPTZ     NOT NULL,
    updated_at  TIMESTAMPTZ     NOT NULL,
    CONSTRAINT pk_department PRIMARY KEY (code),
    CONSTRAINT un_department_name UNIQUE (name)
);

CREATE SEQUENCE backing.department_code_seq
    INCREMENT BY 1
    START WITH 1;