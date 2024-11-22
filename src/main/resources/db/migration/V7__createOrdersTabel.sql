CREATE TABLE orders (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        product_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        total_price DECIMAL(10, 2) NOT NULL,
                        quantity INT NOT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                        CONSTRAINT pk_orders PRIMARY KEY (id),
                        CONSTRAINT fk_orders_product FOREIGN KEY (product_id) REFERENCES products(id),
                        CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);