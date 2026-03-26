CREATE TABLE cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255)
);

CREATE TABLE cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
        cart_id BIGINT,
    product_id BIGINT,
    product_name VARCHAR(255),
    price DOUBLE,
    quantity INT
);