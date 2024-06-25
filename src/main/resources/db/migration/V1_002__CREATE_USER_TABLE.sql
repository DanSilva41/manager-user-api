CREATE TABLE IF NOT EXISTS security."user" (
    code            INT             NOT NULL,
    username        VARCHAR(20)     NOT NULL,
    password        VARCHAR(60)     NOT NULL,
    active          BOOLEAN         NOT NULL,
    person_code     INT             NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (code),
    CONSTRAINT un_user_username UNIQUE (username),
    CONSTRAINT fk_person_code_user FOREIGN KEY (person_code) REFERENCES backing.person(code)
);
