INSERT INTO backing.department (code, name, description, version, created_at, updated_at)
VALUES (1, 'FINANCEIRO', 'Setor responsável pela contabilidade e prestação de contas', 1,
        '2024-06-26 18:51:25.977576 +00:00',
        '2024-06-26 18:51:25.977672 +00:00');

INSERT INTO backing.person (code, first_name, last_name, email, version, created_at, updated_at)
VALUES (1, 'John', 'Allister', 'john.allister@gmail.com', 1, '2024-06-26 18:51:25.977576 +00:00',
        '2024-06-26 18:51:25.977672 +00:00');

INSERT INTO security."user" (code, username, password, active, person_code, department_code, version, created_at,
                             updated_at)
VALUES (1, 'user001', 'p@ssw0rd', true, 1, 1, 1, '2024-06-26 18:51:25.985665 +00:00',
        '2024-06-26 18:51:25.985683 +00:00');
