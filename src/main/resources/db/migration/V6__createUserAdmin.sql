-- login: admin
-- senha: 1234
INSERT INTO users (username, password, role_id)
VALUES ('admin', '$2a$10$NcbdRAuORnEbKyn.14QSYexXJ0Ps4UQDSmbYb9v20rD4AVQB/ydtC',
        (SELECT id FROM roles WHERE name = 'ADMIN'));