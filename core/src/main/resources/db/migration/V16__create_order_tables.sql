-- V16__create_order_tables.sql
-- Create wallets table

CREATE TABLE orders (
    id VARCHAR(36) PRIMARY KEY,

    -- ==================== Audit Fields ====================
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    -- ==================== Basic Information ====================
    tracking_code VARCHAR(50) NOT NULL UNIQUE,
    order_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,

    -- ==================== User Information ====================
    customer_id VARCHAR(36) NOT NULL,
    sender_id VARCHAR(36) NOT NULL,

    -- ==================== Sender Information ====================
    sender_name VARCHAR(255) NOT NULL,
    sender_phone VARCHAR(20) NOT NULL,
    sender_address TEXT NOT NULL,
    sender_ward_id VARCHAR(36) NOT NULL,
    sender_province_id VARCHAR(36) NOT NULL,

    -- ==================== Recipient Information ====================
    recipient_name VARCHAR(255) NOT NULL,
    recipient_phone VARCHAR(20) NOT NULL,
    recipient_address TEXT NOT NULL,
    recipient_ward_id VARCHAR(36) NOT NULL,
    recipient_province_id VARCHAR(36) NOT NULL,

    -- ==================== Package Information ====================
    description TEXT,
    weight REAL NOT NULL,
    dimensions JSONB,
    value_declared NUMERIC(19,2),
    fragile BOOLEAN NOT NULL DEFAULT FALSE,
    requires_signature BOOLEAN NOT NULL DEFAULT FALSE,

    -- ==================== Pricing Information ====================
    shipping_fee NUMERIC(19,2) NOT NULL,
    insurance_fee NUMERIC(19,2) NOT NULL,
    total_amount NUMERIC(19,2) NOT NULL,

    -- ==================== Delivery Information ====================
    assigned_driver_id VARCHAR(36),
    depot_id VARCHAR(36),
    estimated_delivery_date VARCHAR(255),
    actual_delivery_date VARCHAR(255),

    -- ==================== Notes ====================
    notes TEXT,
    rejection_reason TEXT,

    -- ==================== Foreign Keys ====================
    CONSTRAINT fk_orders_sender_province FOREIGN KEY (sender_province_id) REFERENCES provinces(id),
    CONSTRAINT fk_orders_sender_ward FOREIGN KEY (sender_ward_id) REFERENCES wards(id),
    CONSTRAINT fk_orders_recipient_province FOREIGN KEY (recipient_province_id) REFERENCES provinces(id),
    CONSTRAINT fk_orders_recipient_ward FOREIGN KEY (recipient_ward_id) REFERENCES wards(id)
);

CREATE INDEX idx_orders_customer_id
    ON orders(customer_id);

CREATE INDEX idx_orders_sender_id
    ON orders(sender_id);

CREATE INDEX idx_orders_status
    ON orders(status);

CREATE INDEX idx_orders_type
    ON orders(order_type);

CREATE INDEX idx_orders_created_at
    ON orders(created_at);

CREATE INDEX idx_orders_tracking_code
    ON orders(tracking_code);