CREATE SCHEMA IF NOT EXISTS order_service AUTHORIZATION postgres;

CREATE TABLE IF NOT EXISTS order_service.orders (
    order_id UUID PRIMARY KEY,
    shopping_cart_id UUID,
    payment_id UUID,
    delivery_id UUID,
    state VARCHAR(50),
    delivery_weight DOUBLE PRECISION,
    delivery_volume DOUBLE PRECISION,
    fragile BOOLEAN,
    total_price DECIMAL(19, 2),
    delivery_price DECIMAL(19, 2),
    product_price DECIMAL(19, 2)
);

CREATE TABLE IF NOT EXISTS order_service.order_products (
    order_id UUID REFERENCES order_service.orders(order_id),
    product_id UUID,
    quantity BIGINT,
    PRIMARY KEY (order_id, product_id)
);
