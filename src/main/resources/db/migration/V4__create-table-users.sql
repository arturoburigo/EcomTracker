CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role_id BIGINT,
                       FOREIGN KEY (role_id) REFERENCES roles(id)
);
