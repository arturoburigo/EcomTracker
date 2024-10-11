CREATE TABLE products (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          name VARCHAR(255) NOT NULL,
                          description VARCHAR(255) NULL,
                          color VARCHAR(50) NULL,
                          size VARCHAR(50) NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          quantity INT NOT NULL,
                          active BOOLEAN DEFAULT TRUE,
                          category_id BIGINT NOT NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id)
);
