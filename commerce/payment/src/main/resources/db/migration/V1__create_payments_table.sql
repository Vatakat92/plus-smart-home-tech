CREATE SCHEMA IF NOT EXISTS payment AUTHORIZATION postgres;

CREATE TABLE IF NOT EXISTS payment.payments (
    payment_id UUID PRIMARY KEY,
    total_payment DECIMAL(19, 2),
    delivery_total DECIMAL(19, 2),
    fee_total DECIMAL(19, 2),
    payment_state VARCHAR(50)
);
