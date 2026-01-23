CREATE SCHEMA IF NOT EXISTS shopping_cart AUTHORIZATION postgres;

CREATE TABLE IF NOT EXISTS shopping_cart.shopping_carts (
    cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) UNIQUE NOT NULL,
    products JSONB DEFAULT '{}',
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);