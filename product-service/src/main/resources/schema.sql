CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    price DOUBLE NOT NULL,
    stock INT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);