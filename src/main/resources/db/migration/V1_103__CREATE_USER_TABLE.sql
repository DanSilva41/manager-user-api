CREATE TABLE IF NOT EXISTS security."user" (
    code            INT             NOT NULL,
    username        VARCHAR(20)     NOT NULL,
    password        VARCHAR(60)     NOT NULL,
    active          BOOLEAN         NOT NULL,
    person_code     INT             NOT NULL,
    department_code INT             NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL,
    updated_at      TIMESTAMPTZ     NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (code),
    CONSTRAINT un_user_username UNIQUE (username),
    CONSTRAINT un_person_code_user UNIQUE (person_code),
    CONSTRAINT fk_person_code_user FOREIGN KEY (person_code) REFERENCES backing.person(code),
    CONSTRAINT fk_department_code_user FOREIGN KEY (department_code) REFERENCES backing.department(code)
);

CREATE SEQUENCE security.user_code_seq
    INCREMENT BY 1
    START WITH 1;