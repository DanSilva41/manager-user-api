-- FINANCEIRO
INSERT INTO backing.department (code, name, description, created_at, updated_at)
VALUES (1, 'FINANCEIRO', 'Setor responsável pela contabilidade e prestação de contas', '2024-06-26 18:51:25.977576 +00:00',
        '2024-06-26 18:51:25.977672 +00:00');

-- MARKETING
INSERT INTO backing.department (code, name, description, created_at, updated_at)
VALUES (2, 'MARKETING', 'Setor responsável pelas mídias sociais e marca da empresa', '2024-06-27 15:18:25.977576 +00:00',
        '2024-06-27 15:18:25.977672 +00:00');

-- John Allister
INSERT INTO backing.person (code, first_name, last_name, email, created_at, updated_at)
    VALUES (1, 'John', 'Allister', 'john.allister@gmail.com', '2024-06-26 18:51:25.977576 +00:00', '2024-06-26 18:51:25.977672 +00:00');

INSERT INTO security."user" (code, username, password, active, person_code, department_code, created_at, updated_at)
    VALUES (1, 'user001', 'p@ssw0rd', true, 1, 1, '2024-06-26 18:51:25.985665 +00:00', '2024-06-26 18:51:25.985683 +00:00');

-- Paul Walker
INSERT INTO backing.person (code, first_name, last_name, email, created_at, updated_at)
VALUES (2, 'Paul', 'Walker', 'paul.walker@gmail.com', '2024-06-27 18:51:25.977576 +00:00', '2024-06-27 18:51:25.977672 +00:00');

INSERT INTO security."user" (code, username, password, active, person_code, department_code, created_at, updated_at)
VALUES (2, 'paulwalker', 'p@ssw0rd', true, 2, 2, '2024-06-27 18:51:25.985665 +00:00', '2024-06-27 18:51:25.985683 +00:00');
