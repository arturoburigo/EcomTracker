CREATE TABLE categories (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            name VARCHAR(100) NOT NULL,
                            active BOOLEAN DEFAULT TRUE,
                            PRIMARY KEY (id)
);
