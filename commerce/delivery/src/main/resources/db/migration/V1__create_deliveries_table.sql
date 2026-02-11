CREATE SCHEMA IF NOT EXISTS delivery AUTHORIZATION postgres;

CREATE TABLE IF NOT EXISTS delivery.deliveries (
    delivery_id UUID PRIMARY KEY,
    from_country VARCHAR(255),
    from_city VARCHAR(255),
    from_street VARCHAR(255),
    from_house VARCHAR(255),
    from_flat VARCHAR(255),
    to_country VARCHAR(255),
    to_city VARCHAR(255),
    to_street VARCHAR(255),
    to_house VARCHAR(255),
    to_flat VARCHAR(255),
    order_id UUID,
    delivery_state VARCHAR(50)
);
