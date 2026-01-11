CREATE SCHEMA IF NOT EXISTS shopping_store AUTHORIZATION postgres;

CREATE TABLE IF NOT EXISTS shopping_store.products (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name VARCHAR(255) NOT NULL,
    description TEXT,
    image_src VARCHAR(500),
    quantity_state VARCHAR(20) NOT NULL,
    product_state VARCHAR(20) NOT NULL,
    product_category VARCHAR(20) NOT NULL,
    price BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);